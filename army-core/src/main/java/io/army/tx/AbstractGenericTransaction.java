package io.army.tx;

import io.army.lang.Nullable;

public abstract class AbstractGenericTransaction implements GenericTransaction {

    protected final Isolation isolation;

    protected final boolean readonly;

    protected final String name;

    protected final long timeoutMills;

    protected long startMills;

    protected AbstractGenericTransaction(final TransactionOptions options) {
        this.readonly = options.readonly;
        this.isolation = options.isolation;
        assert this.isolation != null;
        this.name = options.name;

        final int timeout = options.timeout;
        if (timeout > 0) {
            this.timeoutMills = timeout * 1000L;
        } else {
            this.timeoutMills = -1L;
        }
    }

    @Nullable
    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final Isolation isolation() {
        return this.isolation;
    }

    @Override
    public final boolean readOnly() {
        return this.readonly;
    }

    @Override
    public final int timeToLiveInSeconds() throws TransactionTimeOutException {
        final long restMills;
        restMills = timeToLiveInMillis();
        return (int) ((restMills % 1000L == 0) ? (restMills / 1000L) : (restMills / 1000L + 1L));
    }

    @Override
    public final long timeToLiveInMillis() throws TransactionTimeOutException {
        final long restMills;
        restMills = restTimeoutMills();
        if (restMills < 0L) {
            String m = String.format("%s timeout,rest %s mills.", this, restMills);
            throw new TransactionTimeOutException(m);
        }
        return restMills;
    }


    @Override
    public final String toString() {
        return String.format("%s[%s] of session[%s] status[%s] isolation:%s readOnly:%s rest:%s ms."
                , this.getClass().getName(), this.name
                , this.session().name(), this.status()
                , this.isolation, this.readonly
                , this.restTimeoutMills());
    }


    private long restTimeoutMills() {
        final long restMills;
        final long startMills = this.startMills;
        if (startMills < 0) {
            if (status() != TransactionStatus.NOT_ACTIVE) {
                throw new IllegalStateException(String.format("startMills[%s] error.", startMills));
            }
            restMills = (int) (this.timeoutMills / 1000L);
        } else {
            restMills = this.timeoutMills - (System.currentTimeMillis() - startMills);
        }
        return restMills;
    }

}
