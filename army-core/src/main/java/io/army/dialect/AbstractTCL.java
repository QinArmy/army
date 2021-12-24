package io.army.dialect;

public abstract class AbstractTCL extends AbstractSql implements TclDialect {

    protected AbstractTCL(Dialect dialect) {
        super(dialect);
    }


}
