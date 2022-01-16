package io.army.dialect;

import io.army.criteria.Visible;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

public abstract class _BaseSqlContext implements _StmtContext {

    protected final _Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected _BaseSqlContext(_Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();
    }

    protected _BaseSqlContext(_BaseSqlContext outerContext) {
        this.dialect = outerContext.dialect;
        this.visible = outerContext.visible;
        this.sqlBuilder = outerContext.sqlBuilder;
        this.paramList = outerContext.paramList;
    }


    @Override
    public final _Dialect dialect() {
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
    public final Visible visible() {
        return this.visible;
    }


}
