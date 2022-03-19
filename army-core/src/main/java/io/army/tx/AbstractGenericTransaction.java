package io.army.tx;

import io.army.lang.Nullable;
import io.army.util._Exceptions;

public abstract class AbstractGenericTransaction implements GenericTransaction {

    protected final Isolation isolation;

    protected final boolean readonly;

    protected final String name;

    protected final long timeoutMills;

    protected final long startMills;

    protected AbstractGenericTransaction(final TransactionOptions options) {
        this.readonly = options.readonly;
        this.isolation = options.isolation;
        assert this.isolation != null;

        final String name = options.name;
        this.name = name == null ? "unnamed" : name;

        final int timeout = options.timeout;
        if (timeout > 0) {
            this.timeoutMills = timeout * 1000L;
            this.startMills = System.currentTimeMillis();
        } else {
            this.timeoutMills = -1;
            this.startMills = -1L;
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
    public final int nextTimeout() throws TransactionTimeOutException {
        final long timeoutMills = this.timeoutMills;
        if (timeoutMills < 1000L) {
            return 0;
        }
        final long restMills;
        restMills = timeoutMills - (System.currentTimeMillis() - this.startMills);
        if (restMills < 0L) {
            throw _Exceptions.timeout((int) (timeoutMills / 1000L), restMills);
        }
        final int timeout;
        if (restMills % 1000L == 0L) {
            timeout = (int) (restMills / 1000L);
        } else {
            timeout = ((int) (restMills / 1000L)) + 1;
        }
        return timeout;
    }


    @Override
    public final String toString() {
        return String.format("[%s name:%s,session:%s,status:%s,isolation:%s,readonly:%s,timeout:%s s,rest:%s ms]"
                , this.getClass().getName(), this.name
                , this.session().name(), this.status()
                , this.isolation, this.readonly
                , this.timeoutMills / 1000L, System.currentTimeMillis() - this.startMills);
    }


}
