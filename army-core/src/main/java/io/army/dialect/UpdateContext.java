package io.army.dialect;

import io.army.wrapper.SimpleSQLWrapper;

public interface UpdateContext extends TableContextSQLContext {

    /**
     * @return always {@link SimpleUpdateSQLWrapper}
     */
    @Override
    SimpleSQLWrapper build();
}
