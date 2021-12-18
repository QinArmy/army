package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.beans.ReadWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

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
        return new ValueInsertContext(insert, tableIndex, domainList, dialect, visible);
    }

    static ValueInsertContext child(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        return new ValueInsertContext(tableIndex, insert, domainList, dialect, visible);
    }

    final SingleTableMeta<?> table;

    final List<FieldMeta<?, ?>> fieldList;

    final Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;
    final List<? extends ReadWrapper> domainList;

    private final _InsertBlockImpl childBlock;

    private ValueInsertContext(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        this.table = (SingleTableMeta<?>) insert.table();
        this.fieldList = DmlUtils.mergeInsertFields(false, insert);
        this.commonExpMap = insert.commonExpMap();
        this.domainList = domainList;

        this.childBlock = null;
    }

    private ValueInsertContext(final byte tableIndex, _ValuesInsert insert
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        super(dialect, tableIndex, visible);

        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) insert.table();
        this.table = childTable.parentMeta();
        this.fieldList = DmlUtils.mergeInsertFields(true, insert);
        this.commonExpMap = insert.commonExpMap();
        this.domainList = domainList;

        final List<FieldMeta<?, ?>> childFieldList;
        childFieldList = DmlUtils.mergeInsertFields(false, insert);
        this.childBlock = new _InsertBlockImpl(childTable, childFieldList, domainList);
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
        return Stmts.simple(this.sqlBuilder.toString(), this.paramList);
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


    private static final class _InsertBlockImpl implements _InsertBlock {

        private final ChildTableMeta<?> table;

        private final List<FieldMeta<?, ?>> fieldList;

        private final List<? extends ReadWrapper> domainList;

        private _InsertBlockImpl(ChildTableMeta<?> table, List<FieldMeta<?, ?>> fieldList
                , List<? extends ReadWrapper> domainList) {
            this.table = table;
            this.fieldList = fieldList;
            this.domainList = domainList;
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
        public List<? extends ReadWrapper> domainList() {
            return this.domainList;
        }

    }


}
