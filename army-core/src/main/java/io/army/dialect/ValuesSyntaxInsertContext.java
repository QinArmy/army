package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.criteria.CriteriaException;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Selection;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.stmt.InsertStmtParams;
import io.army.util._Exceptions;

import java.util.*;

abstract class ValuesSyntaxInsertContext extends StatementContext implements _ValueInsertContext, InsertStmtParams {


    final boolean migration;

    final NullHandleMode nullHandleMode;

    final boolean preferLiteral;

    final TableMeta<?> table;

    final List<FieldMeta<?>> fieldList;

    final Map<FieldMeta<?>, _Expression> commonExpMap;

    final boolean duplicateKeyClause;

    /**
     * {@link #table} instanceof {@link  SingleTableMeta} and  dialect support returning clause nad generated key.
     */
    final PrimaryFieldMeta<?> returnId;

    /**
     * @see #returnId
     */
    final String idSelectionAlias;

    final FieldMeta<?> discriminator;

    final int discriminatorValue;


    /**
     * <p>
     * For {@link  io.army.meta.SingleTableMeta}
     * </p>
     */
    ValuesSyntaxInsertContext(ArmyDialect dialect, _Insert._ValuesSyntaxInsert stmt, Visible visible) {
        super(dialect, stmt.isPreferLiteral(), visible);

        this.migration = stmt.isMigration();
        final NullHandleMode handleMode = stmt.nullHandle();
        if (handleMode == null) {
            this.nullHandleMode = NullHandleMode.INSERT_DEFAULT;
        } else {
            this.nullHandleMode = handleMode;
        }
        this.preferLiteral = stmt.isPreferLiteral();
        this.commonExpMap = stmt.defaultExpMap();

        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;

        final TableMeta<?> domainTable = stmt.table();
        if (domainTable instanceof SimpleTableMeta) {
            this.discriminator = null;
            this.discriminatorValue = 0;
        } else {
            this.discriminator = domainTable.discriminator();
            this.discriminatorValue = domainTable.discriminatorValue();
        }
        if (domainTable instanceof ChildTableMeta) {
            this.table = ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            this.table = domainTable;
        }

        final List<FieldMeta<?>> fieldList = stmt.fieldList();
        if (fieldList.size() == 0) {
            this.fieldList = castFieldList(this.table);
        } else {
            this.fieldList = mergeFieldList(this.table, fieldList);
        }

        final PrimaryFieldMeta<?> idField = this.table.id();
        if (idField.generatorType() != GeneratorType.POST || !dialect.supportInsertReturning()) {
            this.returnId = null;
            this.idSelectionAlias = null;
        } else if (stmt instanceof _Insert._ReturningInsert) {
            //TODO
            throw new UnsupportedOperationException();
        } else {
            this.returnId = idField;
            this.idSelectionAlias = idField.fieldName();
        }

    }


    /**
     * <p>
     * For {@link  io.army.meta.ChildTableMeta}
     * </p>
     */
    ValuesSyntaxInsertContext(_Insert._ValuesSyntaxInsert stmt, ArmyDialect dialect, Visible visible) {
        super(dialect, stmt.isPreferLiteral(), visible);

        this.migration = stmt.isMigration();
        final NullHandleMode handleMode = stmt.nullHandle();
        if (handleMode == null) {
            this.nullHandleMode = NullHandleMode.INSERT_DEFAULT;
        } else {
            this.nullHandleMode = handleMode;
        }
        this.preferLiteral = stmt.isPreferLiteral();
        this.commonExpMap = stmt.defaultExpMap();

        this.duplicateKeyClause = stmt instanceof _Insert._DuplicateKeyClause;

        final ChildTableMeta<?> table = (ChildTableMeta<?>) stmt.table();

        this.table = table;
        this.discriminator = table.discriminator();
        this.discriminatorValue = table.discriminatorValue();

        final List<FieldMeta<?>> childFieldList = stmt.childFieldList();
        if (childFieldList.size() == 0) {
            this.fieldList = castFieldList(this.table);
        } else {
            this.fieldList = mergeFieldList(this.table, childFieldList);
        }
        this.returnId = null;
        this.idSelectionAlias = null;

    }

    @Override
    public final TableMeta<?> table() {
        return this.table;
    }

    @Override
    public final void appendFieldList() {
        final List<FieldMeta<?>> fieldList = this.fieldList;
        final int fieldSize = fieldList.size();
        final ArmyDialect dialect = this.dialect;
        final StringBuilder sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_LEFT_PAREN);

        FieldMeta<?> field;
        for (int i = 0, actualIndex = 0; i < fieldSize; i++) {
            field = fieldList.get(i);
            if (!field.insertable()) {
                // fieldList have be checked,fieldList possibly is io.army.meta.TableMeta.fieldList()
                continue;
            }
            if (actualIndex > 0) {
                sqlBuilder.append(_Constant.SPACE_COMMA);
            }
            dialect.safeObjectName(fieldList.get(i), sqlBuilder);
            actualIndex++;
        }
        sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
    }


    @Override
    public final void appendReturnIdIfNeed() {
        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId == null) {
            return;
        }
        final StringBuilder sqlBuilder;
        sqlBuilder = this.sqlBuilder
                .append(_Constant.SPACE_RETURNING)
                .append(_Constant.SPACE);

        final ArmyDialect dialect = this.dialect;
        //TODO for dialect table alias
        dialect.safeObjectName(returnId, sqlBuilder)
                .append(_Constant.SPACE_AS_SPACE);

        dialect.identifier(this.idSelectionAlias, sqlBuilder);
    }

    @Override
    public final PrimaryFieldMeta<?> idField() {
        PrimaryFieldMeta<?> field = this.returnId;
        if (field == null) {
            final TableMeta<?> table = this.table;
            if (table instanceof ChildTableMeta) {
                //no bug,never here
                throw new IllegalStateException();
            }
            field = table.id();
        }
        return field;
    }

    @Override
    public final String idReturnAlias() {
        String alias = this.idSelectionAlias;
        if (alias == null) {
            final TableMeta<?> table = this.table;
            if (table instanceof ChildTableMeta) {
                //no bug,never here
                throw new IllegalStateException();
            }
            alias = table.id().fieldName();
        }
        return alias;
    }

    @Override
    public final List<Selection> selectionList() {
        //TODO
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    static <T extends IDomain> List<FieldMeta<?>> castFieldList(final TableMeta<T> table) {
        final List<?> list;
        list = table.fieldList();
        return (List<FieldMeta<?>>) list;
    }

    static <T extends IDomain> List<FieldMeta<?>> mergeFieldList(final TableMeta<T> table
            , final List<FieldMeta<?>> fieldList) {
        final List<FieldMeta<?>> mergeFieldList = new ArrayList<>(fieldList.size());
        final Map<FieldMeta<?>, Boolean> fieldMap = new HashMap<>();
        for (FieldMeta<?> field : fieldList) {
            if (!field.insertable()) {
                throw _Exceptions.nonInsertableField(field);
            }
            if (field.tableMeta() != table) {
                throw _Exceptions.unknownColumn(null, field);
            }
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) != null) {
                String m = String.format("%s duplication.", field);
                throw new CriteriaException(m);
            }
            mergeFieldList.add(field);
        }

        for (FieldMeta<?> field : table.fieldChain()) {
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
        }

        FieldMeta<?> field;

        field = table.id();
        if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
            mergeFieldList.add(field);
        }

        if (table instanceof ParentTableMeta) {
            field = table.discriminator();
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
        }

        if (!(table instanceof ChildTableMeta)) {
            field = table.getField(_MetaBridge.CREATE_TIME);
            if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                mergeFieldList.add(field);
            }
            if (table.containField(_MetaBridge.UPDATE_TIME)) {
                field = table.getField(_MetaBridge.UPDATE_TIME);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }
            if (table.containField(_MetaBridge.VERSION)) {
                field = table.getField(_MetaBridge.VERSION);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }
            if (table.containField(_MetaBridge.VISIBLE)) {
                field = table.getField(_MetaBridge.VISIBLE);
                if (fieldMap.putIfAbsent(field, Boolean.TRUE) == null) {
                    mergeFieldList.add(field);
                }
            }

        }


        if (mergeFieldList.size() == 0) {
            String m = String.format("%s no insertable filed.", table);
            throw new CriteriaException(m);
        }
        return Collections.unmodifiableList(mergeFieldList);
    }

    static IllegalStateException parentStmtDontExecute(PrimaryFieldMeta<?> filed) {
        String m = String.format("parent stmt don't execute so %s parameter value is null", filed);
        return new IllegalStateException(m);
    }


}
