package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.Index;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.domain.IDomain;
import io.army.meta.Field;
import io.army.meta.MappingMode;
import io.army.meta.MetaAssert;
import io.army.meta.TableMeta;
import io.army.util.MetaUtils;
import io.army.util.Pair;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * created  on 2018/11/19.
 */
public final class DefaultTable<T extends IDomain> implements TableMeta<T> {

    private final Class<T> entityClass;

    private final int fieldCount;

    private final String tableName;

    private final List<String> indexList;

    private final List<String> uniqueList;

    private final boolean immutable;

    private final String comment;

    private final MappingMode mappingMode;

    private final Charset charset;

    private List<Field<T, ?>> fieldList = new ArrayList<>();

    private boolean usable = false;

    private final AtomicReference<Field<T, ?>> primaryKeyReference = new AtomicReference<>();

    public DefaultTable(Class<T> entityClass) throws MetaException {
        this.entityClass = entityClass;

        try {
            Table tableMeta = MetaUtils.tableMeta(entityClass);
            this.tableName = tableMeta.name();
            this.comment = tableMeta.comment();

            this.immutable = MetaUtils.immutable(entityClass);
            this.fieldCount = MetaUtils.fieldCount(entityClass);
            this.mappingMode = MetaUtils.mappingMode(entityClass);
            this.charset = Charset.forName(tableMeta.charset());

            Pair<List<String>, List<String>> pair = crateIndexNames(tableMeta);
            this.indexList = pair.getFirst();
            this.uniqueList = pair.getSecond();
        } catch (RuntimeException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, e.getMessage());
        }

    }


    @Override
    public List<String> indexPropList() {
        return indexList;
    }

    @Override
    public List<String> uniquePropList() {
        return uniqueList;
    }

    @Override
    public Class<T> javaType() {
        return entityClass;
    }

    @Override
    public String tableName() {
        return tableName;
    }

    @Override
    public boolean immutable() {
        return immutable;
    }

    @Override
    public int fieldCount() {
        return fieldCount;
    }

    @Override
    public String comment() {
        return comment;
    }

    @Override
    public Field<T, ?> primaryKey() {
        Field<T, ?> primaryKey = primaryKeyReference.get();
        if (primaryKey == null) {
            throw new MetaException(ErrorCode.META_ILLEGALITY, "Table[%s] not config primary key");
        }
        return primaryKey;
    }


    @Override
    public MappingMode mappingMode() {
        return mappingMode;
    }

    @Override
    public List<Field<T, ?>> fieldList() {
        return fieldList;
    }


    @Override
    public String createSql(Dialect dialect) {
        return null;
    }

    @Override
    public Charset charset() {
        return charset;
    }


    void addField(Field<T, ?> field) {
        if (field == null) {
            throw new MetaException(ErrorCode.META_ILLEGALITY, "Table[%s] field  required",
                    tableName());
        }
        if (fieldList.size() >= fieldCount) {
            throw new MetaException(ErrorCode.META_ILLEGALITY, "Table[%s] field count[%s] error,actual[%s]. ",
                    tableName(),
                    fieldCount,
                    fieldList.size()
            );
        }

        try {
            MetaAssert.assertMetaMatch(entityClass, this, field);
        } catch (MetaException e) {
            e.printStackTrace();
        }

        fieldList.add(field);

        if (field.isPrimary()) {
            setPrimaryKey(field);
        }
        if (fieldList.size() == fieldCount) {
            // add field finish
            fieldAddFinishEvent();
        }
    }

    private Pair<List<String>, List<String>> crateIndexNames(Table tableMeta) {
        List<String> indexList = new ArrayList<>(6);
        List<String> uniqueList = new ArrayList<>(6);

        Set<String> indexNameSet = new HashSet<>();
        for (Index index : tableMeta.indexes()) {
            if (indexNameSet.contains(index.name())) {
                throw new MetaException(ErrorCode.META_ERROR, "Table[%s] index name[%s] duplicate",
                        tableMeta.name(), index.name());
            }

            indexNameSet.add(index.name());

            Collections.addAll(indexList, index.columnList());
            if (index.unique()) {
                Collections.addAll(uniqueList, index.columnList());
            }
        }

        return new Pair<>(Collections.unmodifiableList(indexList),
                Collections.unmodifiableList(uniqueList));

    }

    @Override
    public String schema() {
        return null;
    }

    private void fieldAddFinishEvent() {
        validIndex();
        fieldList = Collections.unmodifiableList(fieldList);
        this.usable = true;
    }

    @Override
    public <F> Field<T, F> getField(String propName, Class<F> propClass) {
        return null;
    }

    private void setPrimaryKey(Field<T, ?> primaryKey) throws MetaException {
        if (!primaryKeyReference.compareAndSet(null, primaryKey)) {
            throw new MetaException(ErrorCode.META_ERROR, "Table[%s] primaryKey duplicate set",
                    tableName());
        }
    }

    private void validIndex() throws MetaException {
        for (String indexName : indexList) {
            for (Field<T, ?> field : fieldList) {
                if (!indexName.equals(field.fieldName())) {

                    throw new MetaException(ErrorCode.META_ERROR, "Not found index column[%s.%s]",
                            tableName(),
                            field.fieldName()
                    );
                }
            }
        }
    }
}
