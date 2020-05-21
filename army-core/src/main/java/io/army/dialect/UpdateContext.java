package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.SimpleSQLWrapper;
import io.army.wrapper.SimpleUpdateSQLWrapper;

public interface UpdateContext extends TableContextSQLContext {

    /**
     * @return always {@link SimpleUpdateSQLWrapper}
     */
    @Override
    SimpleSQLWrapper build();

    /**
     * @throws UnsupportedOperationException always throw
     */
    @Override
    DomainSQLWrapper build(DomainWrapper domainWrapper) throws UnsupportedOperationException;
}
