package io.army.dialect;

import io.army.annotation.GeneratorType;
import io.army.bean.ObjectAccessor;
import io.army.bean.ObjectAccessorFactory;
import io.army.criteria.NamedParam;
import io.army.criteria.NonNullNamedParam;
import io.army.criteria.NullHandleMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.*;
import java.util.function.Function;

/**
 * <p>
 * This class representing standard value insert context.
 * </p>
 */
final class StandardValueInsertContext extends StmtContext implements _ValueInsertContext {

    static StandardValueInsertContext create(_ValuesInsert insert, ArmyDialect dialect, Visible visible) {
        checkCommonExpMap(insert);
        return new StandardValueInsertContext(insert, dialect, visible);
    }


    private static void checkCommonExpMap(_ValuesInsert insert) {
        final TableMeta<?> table = insert.table();
        for (Map.Entry<FieldMeta<?>, _Expression> e : insert.commonExpMap().entrySet()) {
            _DmlUtils.checkInsertExpField(table, e.getKey(), e.getValue());
        }
    }


    final boolean migration;

    final SingleTableMeta<?> table;

    final List<FieldMeta<?>> fieldList;

    final Map<FieldMeta<?>, _Expression> commonExpMap;

    final ObjectAccessor domainAccessor;

    final List<IDomain> domainList;

    final NullHandleMode nullHandleMode;

    //@see io.army.dialect._DmlUtils.appendStandardValueInsert,for parse comment expression
    IDomain currentDomain;

    private final PrimaryFieldMeta<?> returnId;

    private final ChildBlock childBlock;


    private StandardValueInsertContext(_ValuesInsert insert, ArmyDialect dialect, Visible visible) {
        super(dialect, visible);

        this.migration = insert.isMigration();
        this.commonExpMap = insert.commonExpMap();
        this.domainList = insert.domainList();

        if (!this.migration && insert.fieldList().size() == 0) {
            this.nullHandleMode = insert.nullHandle();
        } else {
            this.nullHandleMode = NullHandleMode.INSERT_NULL;
        }
        final TableMeta<?> table = insert.table();
        this.domainAccessor = ObjectAccessorFactory.forBean(table.javaType());
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
    public List<IDomain> domainList() {
        return this.domainList;
    }

    @Override
    public String safeTableAlias(TableMeta<?> table, String alias) {
        throw new UnsupportedOperationException();
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
            parentStmt = Stmts.post(this.sqlBuilder.toString(), this.paramList, this.domainList, this.domainAccessor
                    , this.table.id());
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
    public boolean migration() {
        return this.migration;
    }

    @Override
    public NullHandleMode nullHandle() {
        return this.nullHandleMode;
    }

    @Override
    public Map<FieldMeta<?>, _Expression> commonExpMap() {
        return this.commonExpMap;
    }

    @Override
    public ObjectAccessor domainAccessor() {
        return this.domainAccessor;
    }

    void onParentEnd() {
        final PrimaryFieldMeta<?> returnId = this.returnId;
        if (returnId != null) {
            final StringBuilder builder;
            builder = this.sqlBuilder
                    .append(_Constant.SPACE_RETURNING)
                    .append(_Constant.SPACE);

            this.dialect.quoteIfNeed(returnId.columnName(), builder)
                    .append(_Constant.SPACE_AS_SPACE);
            this.dialect.quoteIfNeed(returnId.fieldName(), builder);
        }
    }

    @Override
    public _InsertBlock childBlock() {
        return this.childBlock;
    }


    @Override
    List<ParamValue> createParamList() {
        return new ProxyList(this::handleNamedParam);
    }

    private ParamValue handleNamedParam(final NamedParam namedParam) {
        //this.currentDomain @see io.army.dialect._DmlUtils.appendStandardValueInsert
        final IDomain domain = this.currentDomain;
        assert domain != null;
        this.currentDomain = null; //clear for next
        final Object value;
        value = this.domainAccessor.get(domain, namedParam.name());
        if (value == null && namedParam instanceof NonNullNamedParam) {
            throw _Exceptions.nonNullNamedParam((NonNullNamedParam) namedParam);
        }
        return ParamValue.build(namedParam.paramMeta(), value);
    }


    private static final class ChildBlock extends StmtContext implements _InsertBlock, _SqlContext {

        private final ChildTableMeta<?> table;

        private final List<FieldMeta<?>> fieldList;

        private final StandardValueInsertContext parentContext;

        private ChildBlock(ChildTableMeta<?> table, List<FieldMeta<?>> fieldList
                , StandardValueInsertContext parentContext) {
            super(parentContext.dialect, parentContext.visible);
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
        public String safeTableAlias(TableMeta<?> table, String alias) {
            throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException("child block don't support");
        }

        @Override
        List<ParamValue> createParamList() {
            // here,now this.parentContext is null.
            return new ProxyList(this::handleNamedParam);
        }

        private ParamValue handleNamedParam(NamedParam namedParam) {
            return this.parentContext.handleNamedParam(namedParam);
        }


    }//ChildBlock

    private static final class ProxyList implements List<ParamValue> {

        private final List<ParamValue> paramList = new ArrayList<>();

        private final Function<NamedParam, ParamValue> function;

        private ProxyList(Function<NamedParam, ParamValue> function) {
            this.function = function;
        }

        @Override
        public int size() {
            return this.paramList.size();
        }

        @Override
        public boolean isEmpty() {
            return this.paramList.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.paramList.contains(o);
        }

        @Override
        public Iterator<ParamValue> iterator() {
            return this.paramList.iterator();
        }

        @Override
        public Object[] toArray() {
            return this.paramList.toArray();
        }

        @SuppressWarnings("all")
        @Override
        public <T> T[] toArray(T[] a) {
            return this.paramList.toArray(a);
        }

        @Override
        public boolean add(final ParamValue paramValue) {
            final ParamValue actual;
            if (paramValue instanceof NamedParam) {
                actual = this.function.apply((NamedParam) paramValue);
            } else {
                actual = paramValue;
            }
            return this.paramList.add(actual);
        }

        @Override
        public boolean remove(Object o) {
            return this.paramList.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.paramList.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends ParamValue> c) {
            return this.paramList.addAll(c);
        }

        @Override
        public boolean addAll(int index, Collection<? extends ParamValue> c) {
            return this.paramList.addAll(index, c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.paramList.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.paramList.retainAll(c);
        }

        @Override
        public void clear() {
            this.paramList.clear();
        }

        @Override
        public ParamValue get(int index) {
            return this.paramList.get(index);
        }

        @Override
        public ParamValue set(int index, ParamValue element) {
            return this.paramList.set(index, element);
        }

        @Override
        public void add(int index, ParamValue element) {
            this.paramList.add(index, element);
        }

        @Override
        public ParamValue remove(int index) {
            return this.paramList.remove(index);
        }

        @Override
        public int indexOf(Object o) {
            return this.paramList.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return this.paramList.lastIndexOf(o);
        }

        @Override
        public ListIterator<ParamValue> listIterator() {
            return this.paramList.listIterator();
        }

        @Override
        public ListIterator<ParamValue> listIterator(int index) {
            return this.paramList.listIterator(index);
        }

        @Override
        public List<ParamValue> subList(int fromIndex, int toIndex) {
            return this.paramList.subList(fromIndex, toIndex);
        }

    }//ProxyList


}
