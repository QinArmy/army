package io.army.dialect;

public abstract class AbstractTCL extends AbstractSQL implements TclDialect {

    protected AbstractTCL(Dialect dialect) {
        super(dialect);
    }


}
