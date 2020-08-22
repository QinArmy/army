package io.army.wrapper;

import io.army.codec.StatementType;
import io.army.criteria.Selection;

import java.util.List;

public interface SimpleSQLWrapper extends GenericSimpleWrapper {

    /**
     * @return a unmodifiable list
     */
    List<ParamWrapper> paramList();

    /**
     * @return a unmodifiable list
     */
    List<Selection> selectionList();

    static Builder builder() {
        return new SimpleSQLWrapperImpl.Builder();
    }

    interface Builder {

        Builder sql(String sql);

        Builder paramList(List<ParamWrapper> paramList);

        Builder statementType(StatementType statementType);

        Builder selectionList(List<Selection> selectionList);

        Builder hasVersion(boolean hasVersion);

        SimpleSQLWrapper build();
    }

}
