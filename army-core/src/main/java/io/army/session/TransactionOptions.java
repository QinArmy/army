package io.army.session;

@Deprecated
public abstract class TransactionOptions {

    protected String name;

    protected Isolation isolation;

    protected boolean readonly;

    protected int timeout;


}
