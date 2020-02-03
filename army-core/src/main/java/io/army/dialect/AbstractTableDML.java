package io.army.dialect;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.BeanWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.beans.ReadonlyWrapper;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import jdk.nashorn.internal.ir.EmptyNode;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractTableDML implements TableDML {

    private final SQL sql;

    public AbstractTableDML(SQL sql) {
        this.sql = sql;
    }

    /*################################## blow SQL interface method ##################################*/

    @Override
    public final String quoteIfNeed(String identifier) {
        return sql.quoteIfNeed(identifier);
    }

    @Override
    public final boolean isKeyWord(String identifier) {
        return sql.isKeyWord(identifier);
    }

    @Override
    public final ZoneId zoneId() {
        return sql.zoneId();
    }

    @Override
    public final SessionFactory sessionFactory() {
        return sql.sessionFactory();
    }

    /*################################## blow TableDML method ##################################*/

    @Override
    public final List<SQLWrapper> insert(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entityWrapper, "entity required");
        Assert.isTrue(tableMeta.javaType() == entityWrapper.getWrappedClass(), "tableMata and entity not match");

        List<SQLWrapper> sqlWrapperList;
        switch (tableMeta.mappingMode()) {
            case SIMPLE:
                sqlWrapperList = createInsertForSimple(tableMeta, entityWrapper);
                break;
            case CHILD:
                sqlWrapperList = createInsertForChild(tableMeta, entityWrapper);
                break;
            case PARENT:
                sqlWrapperList = createInsertForParent(tableMeta, entityWrapper);
                break;
            default:
                throw new IllegalArgumentException(
                        String.format("unknown MappingMode[%s]", tableMeta.mappingMode()));

        }
        return sqlWrapperList;
    }


    /*################################## blow protected template method ##################################*/

    /*################################## blow protected method ##################################*/

    /*################################## blow private method ##################################*/


    /**
     * @return a modifiable list
     */
    private <T extends IDomain> List<SQLWrapper> createInsertForSimple(TableMeta<T> tableMeta
            , ReadonlyWrapper entityWrapper) {
        StringBuilder nameBuilder = new StringBuilder("INSERT INTO "), valueBuilder = new StringBuilder(" VALUE(");
        final List<ParamWrapper> paramWrapperList = new ArrayList<>();

        nameBuilder.append(quoteIfNeed(tableMeta.tableName()));
        nameBuilder.append("(");

        Object value;
        int count = 0;
        for (FieldMeta<T, ?> fieldMeta : tableMeta.fieldCollection()) {
            if (!fieldMeta.insertalbe()) {
                continue;
            }
            value = entityWrapper.getPropertyValue(fieldMeta.propertyName());
            if (value == null && !fieldMeta.nullable()) {
                continue;
            }
            if (count != 0) {
                nameBuilder.append(",");
                valueBuilder.append(",");
            }
            // name
            nameBuilder.append(quoteIfNeed(fieldMeta.fieldName()));
            // value
            if (isConstant(fieldMeta)) {
                valueBuilder.append(createConstant(fieldMeta));
            } else {
                valueBuilder.append("?");
                paramWrapperList.add(ParamWrapper.build(fieldMeta.mappingType(), value));
            }
            count++;
        }

        nameBuilder.append(")");
        valueBuilder.append(")");

        return Collections.singletonList(
                SQLWrapper.build(
                        nameBuilder.toString() + valueBuilder.toString()
                        , paramWrapperList)
        );

    }

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> createInsertForParent(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        return createInsertForSimple(tableMeta, entityWrapper);
    }

    /**
     * @return a modifiable list
     */
    private List<SQLWrapper> createInsertForChild(TableMeta<?> tableMeta, ReadonlyWrapper entityWrapper) {
        TableMeta<?> parentMeta = tableMeta.parent();
        Assert.state(parentMeta != null, () -> String.format("entity[%s] parentMeta", tableMeta.javaType().getName()));

        List<SQLWrapper> parentSqlList = createInsertForParent(parentMeta, entityWrapper);
        List<SQLWrapper> sqlWrapperList = createInsertForSimple(tableMeta, entityWrapper);

        List<SQLWrapper> actualSqlList = new ArrayList<>(parentSqlList.size() + sqlWrapperList.size());
        actualSqlList.addAll(parentSqlList);
        actualSqlList.addAll(sqlWrapperList);

        return Collections.unmodifiableList(actualSqlList);
    }

    private Object createConstant(FieldMeta<?, ?> fieldMeta) {
        Object value;
        if (TableMeta.VERSION.equals(fieldMeta.propertyName())) {
            value = 0;
        } else if (fieldMeta == fieldMeta.table().discriminator()) {
            value = fieldMeta.table().discriminatorValue();
        } else {
            throw new IllegalArgumentException(String.format("Entity[%s] prop[%s] cannot create constant value"
                    , fieldMeta.table().javaType().getName()
                    , fieldMeta.propertyName()));
        }
        return value;
    }

    private boolean isConstant(FieldMeta<?, ?> fieldMeta) {
        return TableMeta.VERSION.equals(fieldMeta.propertyName())
                || fieldMeta == fieldMeta.table().discriminator()
                ;
    }


}
