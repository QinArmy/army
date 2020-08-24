package io.army.boot;

import io.army.GenericSession;
import io.army.dialect.Dialect;

public abstract class AbstractGenericRmSession implements GenericSession {

    protected final Dialect dialect;

    protected AbstractGenericRmSession(Dialect dialect) {
        this.dialect = dialect;
    }



    /*################################## blow private method ##################################*/


}
