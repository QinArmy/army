package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.CriteriaException;
import io.army.criteria.FieldPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.session.FactoryMode;
import io.army.stmt.ParamValue;
import io.army.stmt.SimpleStmt;
import io.army.stmt.Stmt;
import io.army.stmt.Stmts;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

final class StandardValueInsertContext implements _ValueInsertContext {


    static StandardValueInsertContext create(_ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        final StandardValueInsertContext context;
        if (insert.table() instanceof ChildTableMeta) {
            final StandardValueInsertContext parentContext;
            parentContext = new StandardValueInsertContext(true, insert, tableIndex, domainList, dialect, visible);
            context = new StandardValueInsertContext(parentContext, insert);
        } else {
            context = new StandardValueInsertContext(false, insert, tableIndex, domainList, dialect, visible);
        }
        return context;
    }

    final TableMeta<?> actualTable;

    final TableMeta<?> table;

    final Dialect dialect;

    final StringBuilder sqlBuilder = new StringBuilder();

    private List<ParamValue> paramList = new ArrayList<>();

    private final byte tableIndex;

    private final String tableSuffix;

    final Visible visible;

    private final List<FieldMeta<?, ?>> fields;

    private final Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap;

    private final List<ObjectWrapper> domainList;

     final StandardValueInsertContext parentContext;


    /**
     * <p>
     * create context for simple
     * </p>
     */
    private StandardValueInsertContext(final boolean parent, _ValuesInsert insert, final byte tableIndex
            , List<ObjectWrapper> domainList, Dialect dialect, Visible visible) {
        final TableMeta<?> table = insert.table();
        this.table = table;
        this.fields = DmlUtils.mergeInsertFields(parent, insert);
        this.commonExpMap = insert.commonExpMap();
        this.dialect = dialect;
        if (parent) {
            this.actualTable = ((ChildTableMeta<?>) table).parentMeta();
        } else if (table instanceof ChildTableMeta) {
            throw new IllegalArgumentException("insert error");
        } else {
            this.actualTable = this.table;
        }

        this.visible = visible;
        this.tableIndex = tableIndex;
        this.tableSuffix = DialectUtils.tableSuffix(tableIndex);
        if (dialect.sessionFactory().factoryMode() == FactoryMode.NO_SHARDING) {
            assert tableIndex == 0;
            this.domainList = insert.domainList();
        } else {
            assert tableIndex >= 0;
            switch (domainList.size()) {
                case 0:
                    throw new IllegalArgumentException("domainList is empty");
                case 1:
                    this.domainList = Collections.singletonList(domainList.get(0));
                    break;
                default:
                    this.domainList = Collections.unmodifiableList(domainList);
            }
        }
        this.parentContext = null;

    }

    private StandardValueInsertContext(StandardValueInsertContext parentContext, _ValuesInsert insert) {

        this.table = parentContext.table;
        this.actualTable = this.table;
        this.fields = DmlUtils.mergeInsertFields(false, insert);
        this.visible = parentContext.visible;

        this.dialect = parentContext.dialect;
        this.tableIndex = parentContext.tableIndex;
        this.tableSuffix = parentContext.tableSuffix;
        this.commonExpMap = parentContext.commonExpMap;

        this.domainList = parentContext.domainList;
        this.parentContext = parentContext;
    }


    public List<FieldMeta<?, ?>> fields() {
        return this.fields;
    }

    @Override
    public List<FieldMeta<?, ?>> parentFields() {
        final StandardValueInsertContext parent = this.parentContext;
        if (parent == null) {
            throw new IllegalStateException("No parent");
        }
        return parent.fields;
    }

    public Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap() {
        return this.commonExpMap;
    }


    public List<ObjectWrapper> domainList() {
        return this.domainList;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        throw _Exceptions.unknownTableAlias(tableAlias);
    }

    @Override
    public void appendField(final FieldMeta<?, ?> fieldMeta) {
        if (fieldMeta.tableMeta() != this.actualTable) {
            throw _Exceptions.unknownColumn(null, fieldMeta);
        }
        this.sqlBuilder.append(this.dialect.safeFieldName(fieldMeta));
    }

    @Override
    public void appendFieldPredicate(FieldPredicate predicate) {
        // criteria error.
        String m = String.format("Standard value insert not support %s.", IPredicate.class.getName());
        throw new CriteriaException(m);
    }

    @Override
    public void appendIdentifier(final String identifier) {
        this.sqlBuilder.append(this.dialect.quoteIfNeed(identifier));
    }

    @Override
    public void appendConstant(ParamMeta paramMeta, Object value) {
        this.sqlBuilder.append(this.dialect.constant(paramMeta.mappingMeta(), value));
    }

    @Override
    public Dialect dialect() {
        return this.dialect;
    }

    @Override
    public StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public void appendParam(ParamValue paramValue) {
        this.paramList.add(paramValue);
    }

    @Override
    public TableMeta<?> tableMeta() {
        return this.table;
    }


    @Override
    public byte tableIndex() {
        return this.tableIndex;
    }

    @Override
    public String tableSuffix() {
        return this.tableSuffix;
    }


    @Override
    public Stmt build() {
        final StandardValueInsertContext parentContext = this.parentContext;
        final SimpleStmt thisStmt;
        final Stmt stmt;
        thisStmt = Stmts.simple(this.sqlBuilder.toString(), this.paramList);
        if (parentContext == null) {
            stmt = thisStmt;
        } else {
            stmt = Stmts.pair((SimpleStmt) parentContext.build(), thisStmt);
        }
        return stmt;
    }


}
