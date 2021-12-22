package io.army.dialect;

import io.army.criteria.Visible;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

@Deprecated
abstract class DomainDmlContext implements _DmlContext {

    protected final Dialect dialect;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected final byte tableIndex;

    private final String tableSuffix;

    protected final Visible visible;


    protected DomainDmlContext(final byte tableIndex
            , Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;

        this.tableIndex = tableIndex;
        this.tableSuffix = _DialectUtils.tableSuffix(tableIndex);
        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();


    }


    @Override
    public final Visible visible() {
        return this.visible;
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
    public final void appendParam(ParamValue paramValue) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(Constant.PLACEHOLDER);
        this.paramList.add(paramValue);
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
