package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectWrapper;
import io.army.bean.ReadWrapper;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.*;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
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
final class StandardValueInsertContext extends _BaseSqlContext implements _ValueInsertContext {

    static StandardValueInsertContext create(_ValuesInsert insert, _Dialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new StandardValueInsertContext(insert, dialect, visible);
    }


    private static void checkCommonExpMap(_ValuesInsert insert) {
        final TableMeta<?> table = insert.table();
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.commonExpMap().entrySet()) {
            _DmlUtils.checkInsertExpField(table, e.getKey(), e.getValue());
        }
    }

    final SingleTableMeta<?> table;

    final List<FieldMeta<?>> fieldList;

    final Map<FieldMeta<?>, _Expression> commonExpMap;

    final List<ObjectWrapper> domainList;

    final NullHandleMode nullHandleMode;

    private final PrimaryFieldMeta<?> returnId;

    private final ChildBlock childBlock;


    private StandardValueInsertContext(_ValuesInsert insert, _Dialect dialect, Visible visible) {
        super(dialect, visible);

        this.commonExpMap = insert.commonExpMap();
        this.domainList = insert.domainList();

        if (insert.fieldList().size() == 0) {
            this.nullHandleMode = insert.nullHandle();
        } else {
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
        }

        final TableMeta<?> table = insert.table();
        if (table instanceof ChildTableMeta) {
            final ChildTableMeta<?> childTable = ((ChildTableMeta<?>) table);
            this.table = childTable.parentMeta();
            this.fieldList = _DmlUtils.mergeInsertFields(true, insert);
            this.childBlock = new ChildBlock(childTable, _DmlUtils.mergeInsertFields(false, insert), this);
        } else {
            this.table = (SingleTableMeta<?>) table;
            this.fieldList = _DmlUtils.mergeInsertFields(false, insert);
            this.childBlock = null;
        }
        if (dialect.supportInsertReturning()) {
            this.returnId = this.table.id();
        } else {
            this.returnId = null;
        }

    }

    @Override
    public _SqlContext getContext() {
        return this;
    }

    @Override
    public List<FieldMeta<?>> fieldLis() {
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
    public void appendField(String tableAlias, FieldMeta<?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(tableAlias, field);
    }

    @Override
    public void appendField(FieldMeta<?> field) {
        // value insert don't support insert any field in expression
        throw _Exceptions.unknownColumn(null, field);
    }

    @Override
    public Stmt build() {

        final PrimaryFieldMeta<?> returnId = this.returnId;
        final SimpleStmt parentStmt;
        if (returnId != null) {
            parentStmt = Stmts.simple(this.sqlBuilder.toString(), this.paramList, returnId);
        } else if (this.table.id().generatorType() == GeneratorType.POST) {
            parentStmt = Stmts.post(this.sqlBuilder.toString(), this.paramList, this.domainList, this.table.id());
        } else {
            parentStmt = Stmts.simple(this.sqlBuilder.toString(), this.paramList);
        }
        final ChildBlock childBlock = this.childBlock;
        final Stmt stmt;
        if (childBlock == null) {
            stmt = parentStmt;
        } else {
            final SimpleStmt childStmt;
            childStmt = Stmts.simple(childBlock.sqlBuilder.toString(), childBlock.paramList);
            stmt = Stmts.pair(parentStmt, childStmt);
        }
        return stmt;
    }

    @Override
    public SingleTableMeta<?> table() {
        return this.table;
    }

    @Override
    public NullHandleMode nullHandle() {
        return this.nullHandleMode;
    }

    @Override
    public Map<FieldMeta<?>, _Expression> commonExpMap() {
        return this.commonExpMap;
    }

    void onParentEnd() {
        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId != null) {
            final StringBuilder builder;
            builder = this.sqlBuilder
                    .append(Constant.SPACE_RETURNING)
                    .append(Constant.SPACE);

            this.dialect.quoteIfNeed(returnId.columnName(), builder)
                    .append(Constant.SPACE_AS_SPACE);
            this.dialect.quoteIfNeed(returnId.fieldName(), builder);
        }
    }

    @Override
    public _InsertBlock childBlock() {
        return this.childBlock;
    }


    private static final class ChildBlock implements _InsertBlock, _SqlContext {

        private final ChildTableMeta<?> table;

        private final List<FieldMeta<?>> fieldList;

        private final StandardValueInsertContext parentContext;

        private final StringBuilder sqlBuilder = new StringBuilder();

        private final List<ParamValue> paramList = new ArrayList<>();

        private ChildBlock(ChildTableMeta<?> table, List<FieldMeta<?>> fieldList, StandardValueInsertContext parentContext) {
            this.table = table;
            this.fieldList = fieldList;
            this.parentContext = parentContext;
        }

        @Override
        public _SqlContext getContext() {
            return this;
        }

        @Override
        public ChildTableMeta<?> table() {
            return this.table;
        }

        @Override
        public List<FieldMeta<?>> fieldLis() {
            return this.fieldList;
        }

        @Override
        public void appendField(String tableAlias, FieldMeta<?> field) {
            // value insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(tableAlias, field);
        }

        @Override
        public void appendField(FieldMeta<?> field) {
            // value insert don't support insert any field in expression
            throw _Exceptions.unknownColumn(null, field);
        }


        @Override
        public _Dialect dialect() {
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
