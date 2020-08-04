package io.army.tx;

import io.army.lang.Nullable;

public abstract class AbstractGenericTransaction implements GenericTransaction {

    protected final Isolation isolation;

    protected final boolean readOnly;

    private final String name;

    private final long endMills;

    protected AbstractGenericTransaction(TransactionOption option) {
        this.readOnly = option.readOnly();
        this.isolation = option.isolation();

        this.name = option.name();
        int timeout = option.timeout();
        if (timeout > 0) {
            this.endMills = (System.currentTimeMillis() + timeout * 1000L);
        } else {
            this.endMills = -1;
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
        return this.readOnly;
    }

    @Override
    public final int timeToLiveInSeconds() throws TransactionTimeOutException {
        long liveInMills = timeToLiveInMillis();
        int liveInsSeconds;
        if (liveInMills < 0L) {
            liveInsSeconds = -1;
        } else {
            final long thousand = 1000L;
            liveInsSeconds = (int) (liveInMills / thousand);
            if (liveInsSeconds % thousand != 0) {
                liveInsSeconds++;
            }
        }
        return liveInsSeconds;
    }

    @Override
    public final long timeToLiveInMillis() throws TransactionTimeOutException {
        if (this.endMills < 0L) {
            return -1L;
        }
        long liveInMills = this.endMills - System.currentTimeMillis();
        if (liveInMills < 0) {
            throw new TransactionTimeOutException("transaction[name:%s] timeout,live in mills is %s ."
                    , this.name, liveInMills);
        }
        return liveInMills;
    }

}
