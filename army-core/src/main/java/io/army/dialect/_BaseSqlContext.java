package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.ParamMeta;
import io.army.sharding._RouteUtils;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

public abstract class _BaseSqlContext implements _StmtContext {

    protected final Dialect dialect;

    protected final byte tableIndex;

    protected final String tableSuffix;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected _BaseSqlContext(Dialect dialect, byte tableIndex, Visible visible) {
        this.dialect = dialect;
        this.tableIndex = tableIndex;
        this.visible = visible;
        this.tableSuffix = _RouteUtils.tableSuffix(tableIndex);

        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();
    }


    @Override
    public final byte tableIndex() {
        return this.tableIndex;
    }

    @Override
    public final String tableSuffix() {
        return this.tableSuffix;
    }

    @Override
    public final void appendConstant(ParamMeta paramMeta, Object value) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(this.dialect.constant(paramMeta.mappingType(), value));
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
    public final void appendIdentifier(String identifier) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(this.dialect.quoteIfNeed(identifier));
    }

    @Override
    public final void appendParam(ParamValue paramValue) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(Constant.PLACEHOLDER);
        this.paramList.add(paramValue);
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


}
