package io.army.wrapper;

import java.util.List;

final class SimpleUpdateSQLWrapperImpl extends SQLWrapperImpl implements SimpleUpdateSQLWrapper {

    private final boolean hasVersion;

    SimpleUpdateSQLWrapperImpl(String sql, List<ParamWrapper> paramList, boolean hasVersion) {
        super(sql, paramList);
        this.hasVersion = hasVersion;
    }

    @Override
    public final boolean hasVersion() {
        return this.hasVersion;
    }

}
