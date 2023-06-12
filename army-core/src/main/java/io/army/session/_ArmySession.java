package io.army.session;

import io.army.meta.ChildTableMeta;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

public abstract class _ArmySession implements Session {


    protected final String name;

    protected final boolean readonly;

    protected _ArmySession(String name, boolean readonly) {
        assert _StringUtils.hasText(name);
        this.name = name;
        this.readonly = readonly;
    }


    @Override
    public final String name() {
        return this.name;
    }

    @Override
    public final boolean isReadonlySession() {
        return this.readonly;
    }


    public static int restSecond(final ChildTableMeta<?> domainTable, final long startTime, final int timeout) {
        final int restSeconds;
        final long restMills;
        if (timeout == 0) {
            restSeconds = 0;
        } else if ((restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime)) < 1L) {
            String m = String.format("%s Parent insert completion,but timeout,so no time insert/update child.",
                    domainTable);
            throw new ChildUpdateException(m, _Exceptions.timeout(timeout, restMills));
        } else if ((restMills % 1000L) == 0) {
            restSeconds = (int) (restMills / 1000L);
        } else {
            restSeconds = (int) (restMills / 1000L) + 1;
        }
        return restSeconds;
    }


}
