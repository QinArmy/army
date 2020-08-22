package io.army.wrapper;

import io.army.codec.StatementType;

import java.util.List;

public interface BatchSimpleSQLWrapper extends GenericSimpleWrapper {


    List<List<ParamWrapper>> paramGroupList();

    static Builder builder() {
        return new BatchSimpleSQLWrapperImpl.Builder();
    }

    interface Builder {

        Builder sql(String sql);

        Builder paramGroupList(List<List<ParamWrapper>> paramGroupList);

        Builder statementType(StatementType statementType);

        Builder hasVersion(boolean hasVersion);

        BatchSimpleSQLWrapper build();
    }

}
