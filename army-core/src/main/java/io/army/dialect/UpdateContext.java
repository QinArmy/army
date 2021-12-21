package io.army.dialect;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmts;
import io.army.util.CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This class representing standard single update context.
 * </p>
 */
final class UpdateContext extends _BaseSqlContext implements _SingleUpdateContext {

    static UpdateContext single(_SingleUpdate update, final byte tableIndex, Dialect dialect, Visible visible) {
        return new UpdateContext(update, tableIndex, dialect, visible);
    }

    static UpdateContext child(_SingleUpdate update, final byte tableIndex, Dialect dialect, Visible visible) {
        return new UpdateContext(tableIndex, update, dialect, visible);
    }

    final SingleTableMeta<?> table;

    final String tableAlias;

    final String safeTableAlias;

    final List<FieldMeta<?, ?>> fieldList;

    final List<_Expression<?>> valueExpList;

    final List<_Predicate> predicateList;

    private final ChildSetBlock childSetClause;

    private final boolean multiTableUpdateChild;

    private UpdateContext(_SingleUpdate update, byte tableIndex, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        final SingleTableMeta<?> table = (SingleTableMeta<?>) update.table();
        final List<FieldMeta<?, ?>> fieldList = update.fieldList();
        for (FieldMeta<?, ?> field : fieldList) {
            if (field.tableMeta() != table) {
                throw _Exceptions.unknownColumn(update.tableAlias(), field);
            }
        }
        this.table = table;
        this.fieldList = fieldList;
        this.valueExpList = update.valueExpList();
        this.predicateList = update.predicateList();

        this.tableAlias = update.tableAlias();
        this.safeTableAlias = dialect.quoteIfNeed(this.tableAlias);
        this.multiTableUpdateChild = dialect.multiTableUpdateChild();
        this.childSetClause = null;
    }

    private UpdateContext(byte tableIndex, _SingleUpdate update, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) update.table();
        final SingleTableMeta<?> parentTable = childTable.parentMeta();
        final List<FieldMeta<?, ?>> fieldList = update.fieldList();
        final List<_Expression<?>> valueExpList = update.valueExpList();
        final int fieldCount = fieldList.size();

        final List<FieldMeta<?, ?>> parenFields = new ArrayList<>(), fields = new ArrayList<>();
        final List<_Expression<?>> parentValues = new ArrayList<>(), values = new ArrayList<>();

        FieldMeta<?, ?> field;
        TableMeta<?> belongOf;
        for (int i = 0; i < fieldCount; i++) {
            field = fieldList.get(i);
            belongOf = field.tableMeta();
            if (belongOf == parentTable) {
                parenFields.add(field);
                parentValues.add(valueExpList.get(i));
            } else if (belongOf == childTable) {
                fields.add(field);
                values.add(valueExpList.get(i));
            } else {
                throw _Exceptions.unknownColumn(update.tableAlias(), field);
            }
        }
        this.table = parentTable;
        if (parenFields.size() == 0) {
            this.fieldList = Collections.emptyList();
            this.valueExpList = Collections.emptyList();
        } else if (fields.size() == 0) {
            this.fieldList = fieldList;
            this.valueExpList = valueExpList;
        } else {
            this.fieldList = CollectionUtils.unmodifiableList(parenFields);
            this.valueExpList = CollectionUtils.unmodifiableList(parentValues);
        }
        this.predicateList = update.predicateList();
        final String tableAlias = update.tableAlias();

        this.tableAlias = DialectUtils.parentAlias(tableAlias);
        this.safeTableAlias = this.tableAlias;
        this.multiTableUpdateChild = dialect.multiTableUpdateChild();
        this.childSetClause = new ChildSetBlock(childTable, tableAlias, fieldList, values, this);
    }

    /*################################## blow _SqlContext method ##################################*/

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?, ?> field) {
        final ChildSetBlock childSetClause = this.childSetClause;
        if (childSetClause == null) {
            if (!this.tableAlias.equals(tableAlias)) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
        } else if (!childSetClause.tableAlias.equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public void appendField(final FieldMeta<?, ?> field) {
        final TableMeta<?> belongOf = field.tableMeta();
        if (belongOf == this.table) {// field is parent table column.
            this.sqlBuilder
                    .append(Constant.SPACE)
                    .append(this.safeTableAlias)
                    .append(Constant.POINT)
                    .append(this.dialect.safeColumnName(field.columnName()));
        } else {
            final ChildSetBlock childSetClause = this.childSetClause;
            if (childSetClause == null || belongOf != childSetClause.table()) {
                throw _Exceptions.unknownColumn(null, field);
            }
            // field is child table column
            if (this.multiTableUpdateChild) {// parent and child table in multi-table update statement,eg: MySQL multi-table update
                this.sqlBuilder
                        .append(Constant.SPACE)
                        .append(childSetClause.safeTableAlias)
                        .append(Constant.POINT)
                        .append(this.dialect.safeColumnName(field.columnName()));
            } else {
                //non multi-table update,so convert child table filed as sub query.
                childColumnFromSubQuery(this, childSetClause, field);
            }

        }

    }

    /*################################## blow _UpdateContext method ##################################*/

    @Override
    public boolean setClauseTableAlias() {
        return this.dialect.setClauseTableAlias();
    }

    @Override
    public List<_Predicate> predicates() {
        return this.predicateList;
    }

    /*################################## blow _SingleUpdateContext method ##################################*/

    @Nullable
    @Override
    public _SetBlock childSetClause() {
        return this.childSetClause;
    }

    /*################################## blow _SetClause method ##################################*/

    @Override
    public boolean unionUpdateChild() {
        return this.multiTableUpdateChild;
    }

    @Override
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public boolean hasSelfJoint() {
        //standard single update always false
        return false;
    }

    @Override
    public String safeTableAlias() {
        return this.safeTableAlias;
    }

    @Override
    public List<? extends SetTargetPart> targetParts() {
        return this.fieldList;
    }

    @Override
    public List<? extends SetValuePart> valueParts() {
        return this.valueExpList;
    }

    @Override
    public SimpleStmt build() {
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
    }


    @Override
    public String toString() {
        return "standard domain update context";
    }


    private static final class ChildSetBlock implements _SetBlock {

        private final ChildTableMeta<?> table;

        private final String tableAlias;

        private final String safeTableAlias;

        final List<FieldMeta<?, ?>> fieldList;

        final List<_Expression<?>> valueExpList;

        private final UpdateContext parentContext;

        private ChildSetBlock(ChildTableMeta<?> table, final String tableAlias
                , List<FieldMeta<?, ?>> fieldList, List<_Expression<?>> valueExpList
                , UpdateContext parentContext) {
            this.table = table;
            this.tableAlias = tableAlias;
            this.safeTableAlias = parentContext.dialect.quoteIfNeed(tableAlias);
            this.fieldList = CollectionUtils.unmodifiableList(fieldList);
            this.valueExpList = CollectionUtils.unmodifiableList(valueExpList);
            this.parentContext = parentContext;
        }

        @Override
        public ChildTableMeta<?> table() {
            return this.table;
        }

        @Override
        public byte tableIndex() {
            //must same with parent
            return this.parentContext.tableIndex;
        }

        @Override
        public String tableSuffix() {
            //must same with parent
            return this.parentContext.tableSuffix;
        }

        @Override
        public String tableAlias() {
            return this.tableAlias;
        }

        @Override
        public String safeTableAlias() {
            return this.safeTableAlias;
        }

        @Override
        public boolean hasSelfJoint() {
            return this.parentContext.hasSelfJoint();
        }

        @Override
        public List<FieldMeta<?, ?>> targetParts() {
            return this.fieldList;
        }

        @Override
        public List<_Expression<?>> valueParts() {
            return this.valueExpList;
        }

        @Override
        public void appendField(final String tableAlias, final FieldMeta<?, ?> field) {
            if (!this.tableAlias.equals(tableAlias)) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
            this.appendField(field);
        }

        @Override
        public void appendField(final FieldMeta<?, ?> field) {
            final TableMeta<?> belongOf = field.tableMeta();
            final StringBuilder sqlBuilder = this.parentContext.sqlBuilder;
            final Dialect dialect = this.parentContext.dialect;

            if (belongOf == this.table) {// field is child table column.
                sqlBuilder
                        .append(Constant.SPACE)
                        .append(this.safeTableAlias)
                        .append(Constant.POINT)
                        .append(dialect.safeColumnName(field.columnName()));
            } else if (belongOf == this.parentContext.table) {// field is parent table column.
                final String parentSafeTable = this.parentContext.safeTableAlias;
                if (this.parentContext.multiTableUpdateChild) {// parent and child table in multi-table update statement,eg: MySQL multi-table update
                    sqlBuilder
                            .append(Constant.SPACE)
                            .append(parentSafeTable)
                            .append(Constant.POINT)
                            .append(dialect.safeColumnName(field.columnName()));
                } else {
                    //non multi-table update,so convert parent filed as sub query.
                    parentColumnFromSubQuery(this, field);
                }
            } else {
                throw _Exceptions.unknownColumn(null, field);
            }
        }

        @Override
        public Dialect dialect() {
            return this.parentContext.dialect;
        }

        @Override
        public StringBuilder sqlBuilder() {
            //for update statement,parent update and child update must in same statement.
            return this.parentContext.sqlBuilder;
        }

        @Override
        public void appendParam(ParamValue paramValue) {
            //for update statement,parent update and child update must in same statement.
            this.parentContext.appendParam(paramValue);
        }

        @Override
        public Visible visible() {
            return this.parentContext.visible;
        }
    }


}
