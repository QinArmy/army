package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.beans.ReadWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class ValueInsertContext extends _BaseSqlContext implements _ValueInsertContext {

    static ValueInsertContext single(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new ValueInsertContext(insert, tableIndex, domainList, dialect, visible);
    }

    static ValueInsertContext child(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new ValueInsertContext(tableIndex, insert, domainList, dialect, visible);
    }


    private static void checkCommonExpMap(_ValuesInsert insert) {
        final TableMeta<?> table = insert.table();
        for (Map.Entry<FieldMeta<?, ?>, _Expression<?>> e : insert.commonExpMap().entrySet()) {
            _DmlUtils.checkInsertExpField(table, e.getKey(), e.getValue());
        }
    }

    final SingleTableMeta<?> table;

    final List<FieldMeta<?, ?>> fieldList;

    final Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;
    final List<? extends ReadWrapper> domainList;

    private final ChildBlock childBlock;

    private ValueInsertContext(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        this.table = (SingleTableMeta<?>) insert.table();
        this.fieldList = _DmlUtils.mergeInsertFields(false, insert);
        this.commonExpMap = insert.commonExpMap();
        this.domainList = domainList;

        this.childBlock = null;
    }

    private ValueInsertContext(final byte tableIndex, _ValuesInsert insert
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) insert.table();
        this.table = childTable.parentMeta();
        this.fieldList = _DmlUtils.mergeInsertFields(true, insert);
        this.commonExpMap = insert.commonExpMap();
        this.domainList = domainList;

        this.childBlock = new ChildBlock(childTable, _DmlUtils.mergeInsertFields(false, insert), this);
    }


    @Override
    public List<FieldMeta<?, ?>> fieldLis() {
        return this.fieldList;
    }

    @Override
    public List<? extends ReadWrapper> domainList() {
        return this.domainList;
    }

    @Override
    public int discriminatorValue() {
        final ChildBlock childBlock = this.childBlock;
        return childBlock == null ? this.table.discriminatorValue() : childBlock.table.discriminatorValue();
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?, ?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(null, field);
    }

    @Override
    public Stmt build() {
        final Stmt stmt, parentStmt;
        parentStmt = Stmts.simple(this.sqlBuilder.toString(), this.paramList);
        final ChildBlock childBlock = this.childBlock;
        if (childBlock == null) {
            stmt = parentStmt;
        } else {
            final Stmt childStmt;
            childStmt = Stmts.simple(childBlock.sqlBuilder.toString(), childBlock.paramList);
            stmt = Stmts.group(parentStmt, childStmt);
        }
        return stmt;
    }

    @Override
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap() {
        return this.commonExpMap;
    }

    @Override
    public _InsertBlock childBlock() {
        return this.childBlock;
    }


    private static final class ChildBlock implements _InsertBlock {

        private final ChildTableMeta<?> table;

        private final List<FieldMeta<?, ?>> fieldList;

        private final ValueInsertContext parentContext;

        private final StringBuilder sqlBuilder = new StringBuilder();

        private final List<ParamValue> paramList = new ArrayList<>();

        private ChildBlock(ChildTableMeta<?> table, List<FieldMeta<?, ?>> fieldList, ValueInsertContext parentContext) {
            this.table = table;
            this.fieldList = fieldList;
            this.parentContext = parentContext;
        }

        @Override
        public ChildTableMeta<?> table() {
            return this.table;
        }

        @Override
        public List<FieldMeta<?, ?>> fieldLis() {
            return this.fieldList;
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?, ?> field) {
            // value insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(tableAlias, field);
        }

        @Override
        public void appendField(FieldMeta<?, ?> field) {
            // value insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(null, field);
        }


        @Override
        public Dialect dialect() {
            return this.parentContext.dialect;
        }

        @Override
        public StringBuilder sqlBuilder() {
            return this.sqlBuilder;
        }

        @Override
        public void appendParam(ParamValue paramValue) {
            this.sqlBuilder.append(Constant.SPACE)
                    .append(Constant.PLACEHOLDER);
            this.paramList.add(paramValue);
        }

        @Override
        public Visible visible() {
            return this.parentContext.visible;
        }

    }


}
