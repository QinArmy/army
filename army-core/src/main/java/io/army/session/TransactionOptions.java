package io.army.session;

public abstract class TransactionOptions {

    protected String name;

    protected Isolation isolation;

    protected boolean readonly;

    protected int timeout;


}
