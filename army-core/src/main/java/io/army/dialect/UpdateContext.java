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

    private final _SetBlock childSetClause;

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

        this.tableAlias = _DmlUtils.parentAlias(tableAlias);
        this.safeTableAlias = this.tableAlias;

        if (fields.size() == 0) {
            this.childSetClause = null;
        } else {
            this.childSetClause = _DmlUtils.createSetClause(childTable, tableAlias
                    , dialect.quoteIfNeed(tableAlias), false
                    , fieldList, values);
        }
    }

    /*################################## blow _SqlContext method ##################################*/

    @Override
    public void appendField(final String tableAlias, final FieldMeta<?, ?> field) {
        final _SetBlock childSetClause = this.childSetClause;
        if (childSetClause == null) {
            if (!this.tableAlias.equals(tableAlias)) {
                throw _Exceptions.unknownColumn(tableAlias, field);
            }
        } else if (!childSetClause.tableAlias().equals(tableAlias)) {
            throw _Exceptions.unknownColumn(tableAlias, field);
        }
        this.appendField(field);
    }

    @Override
    public void appendField(final FieldMeta<?, ?> field) {
        final String safeTableAlias;
        final TableMeta<?> belongOf = field.tableMeta();
        if (belongOf == this.table) {
            safeTableAlias = this.safeTableAlias;
        } else {
            final _SetBlock childSetClause = this.childSetClause;
            if (childSetClause == null || belongOf != childSetClause.table()) {
                throw _Exceptions.unknownColumn(null, field);
            }
            safeTableAlias = childSetClause.safeTableAlias();
        }
        this.sqlBuilder
                .append(Constant.SPACE)
                .append(safeTableAlias)
                .append(Constant.POINT)
                .append(this.dialect.safeColumnName(field.columnName()));
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
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public boolean hasSelfJoint() {
        //single update always false
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
        return "standard domain update";
    }


}
