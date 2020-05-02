package io.army.wrapper;


import java.util.List;

class SQLWrapperImpl implements SQLWrapper {

    private final String sql;

    private final List<ParamWrapper> paramList;

    SQLWrapperImpl(String sql, List<ParamWrapper> paramList) {

        this.sql = sql;
        this.paramList = paramList;
    }

    @Override
    public final String sql() {
        return sql;
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return paramList;
    }


}
