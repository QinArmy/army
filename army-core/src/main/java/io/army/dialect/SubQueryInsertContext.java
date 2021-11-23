package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.inner._StandardChildSubQueryInsert;
import io.army.criteria.impl.inner._StandardSubQueryInsert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

final class SubQueryInsertContext extends AbstractTableContextSQLContext implements _ValueInsertContext {

    static SubQueryInsertContext build(_StandardSubQueryInsert insert, Dialect dialect, final Visible visible) {

        String primaryRouteSuffix = TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

        TablesContext tableContext = TablesContext.singleTable(insert, false, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildParent(_StandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

        TablesContext tableContext = TablesContext.singleTable(insert, true, primaryRouteSuffix);
        return new SubQueryInsertContext(dialect, visible, tableContext);
    }

    static SubQueryInsertContext buildChild(_StandardChildSubQueryInsert insert, Dialect dialect
            , Visible visible) {
        String primaryRouteSuffix = TableRouteUtils.subQueryInsertPrimaryRouteSuffix(insert, dialect);

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
    public final void appendField(FieldMeta<?, ?> fieldMeta) {
        if(fieldMeta.tableMeta() != this.physicalTable){
           throw DialectUtils.createUnKnownFieldException(fieldMeta);
        }
       this.doAppendField(null,fieldMeta);
    }

    @Override
    public final SqlBuilder fieldsBuilder() {
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
}
