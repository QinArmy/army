package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

abstract class DomainDmlContext implements _DmlContext {


    protected final TableMeta<?> table;

    protected final String safeTableAlias;

    protected final Dialect dialect;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected final byte tableIndex;

    private final String tableSuffix;

    protected final Visible visible;


    protected DomainDmlContext(TableMeta<?> table, @Nullable String tableAlias, final byte tableIndex
            , Dialect dialect, Visible visible) {
        this.table = table;

        if (tableAlias == null) {
            this.safeTableAlias = null;
        } else {
            this.safeTableAlias = dialect.quoteIfNeed(tableAlias);
        }
        this.dialect = dialect;
        this.visible = visible;

        this.tableIndex = tableIndex;
        this.tableSuffix = DialectUtils.tableSuffix(tableIndex);
        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();


    }


    @Override
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public void appendFieldPredicate(FieldPredicate predicate) {

    }

    @Override
    public final void appendIdentifier(String identifier) {
        this.sqlBuilder.append(this.dialect.quoteIfNeed(identifier));
    }

    @Override
    public void appendConstant(ParamMeta paramMeta, Object value) {
        this.dialect.constant(paramMeta.mappingMeta(), value);
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public void appendParam(ParamValue paramValue) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(Constant.PLACEHOLDER);
        this.paramList.add(paramValue);
    }

    @Override
    public final TableMeta<?> tableMeta() {
        return this.table;
    }

    @Override
    public final byte tableIndex() {
        return this.tableIndex;
    }

    @Override
    public final String tableSuffix() {
        return this.tableSuffix;
    }


}
