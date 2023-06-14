package io.army.session;

import io.army.util._Exceptions;

public abstract class ExecutorSupport {

    protected ExecutorSupport() {
    }


    protected static int restSeconds(final int timeout, final long startTime) {
        if (timeout == 0) {
            return 0;
        }
        if (timeout < 0) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        int restSec;
        final long restMills;
        restMills = (timeout * 1000L) - (System.currentTimeMillis() - startTime);
        if (restMills < 1) {
            throw _Exceptions.timeout(timeout, restMills);
        }
        restSec = (int) (restMills / 1000L);
        if (restMills % 1000L != 0) {
            restSec++;
        }
        return restSec;
    }


}
