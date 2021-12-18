package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._StandardChildSubQueryInsert;
import io.army.criteria.impl.inner._StandardSubQueryInsert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.sharding._TableRouteUtils;
import io.army.stmt.SimpleStmt;

import java.util.List;
import java.util.Map;

final class SubQueryInsertContext extends AbstractTableContextSQLContext implements _ValueInsertContext {

    static SubQueryInsertContext build(_StandardSubQueryInsert insert, Dialect dialect, final Visible visible) {

        String primaryRouteSuffix = _TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

        TablesContext tableContext = TablesContext.singleTable(insert, false, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildParent(_StandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = _TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

        TablesContext tableContext = TablesContext.singleTable(insert, true, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildChild(_StandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = _TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

        TablesContext tableContext = TablesContext.singleTable(insert, false, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static void assertSupportRoute(Dialect dialect) {
//        GenericRmSessionFactory sessionFactory = dialect.sessionFactory();
//        if(!sessionFactory.shardingSubQueryInsert())
//            throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "Sub query insert isn't allowed by SessionFactory[%s]"
//                    , sessionFactory);
    }

    private final TableMeta<?> physicalTable;

    private SubQueryInsertContext(Dialect dialect, Visible visible, TablesContext tableContext) {
        super(dialect, visible, tableContext);
        this.physicalTable = tableContext.singleTable();
    }

    @Override
    public final void appendField(FieldMeta<?, ?> field) {
        if (field.tableMeta() != this.physicalTable) {
            throw DialectUtils.createUnKnownFieldException(field);
        }
        this.doAppendField(null, field);
    }

    public final StringBuilder fieldsBuilder() {
        return this.sqlBuilder;
    }

    @Override
    protected final boolean canAppendTableAlias(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    protected final String parseTableSuffix(TableMeta<?> tableMeta, @Nullable String tableAlias) {
        assertSupportRoute(this.dialect);
        return this.primaryRouteSuffix();
    }

    @Override
    public SimpleStmt build() {
        return null;
    }


    @Override
    public TableMeta<?> tableMeta() {
        return null;
    }

    @Override
    public byte tableIndex() {
        return 0;
    }

    @Override
    public String tableSuffix() {
        return null;
    }

    @Override
    public List<FieldMeta<?, ?>> fields() {
        return null;
    }

    @Override
    public List<FieldMeta<?, ?>> parentFields() {
        return null;
    }

    @Override
    public Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap() {
        return null;
    }

    @Override
    public List<ObjectWrapper> domainList() {
        return null;
    }
}
