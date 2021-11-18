package io.army.util;

import io.army.ArmyException;
import io.army.DialectMode;
import io.army.meta.ServerMeta;
import io.army.session.TimeoutException;
import io.army.stmt.Stmt;
import io.qinarmy.util.ExceptionUtils;

public abstract class Exceptions extends ExceptionUtils {

    protected Exceptions() {
        throw new UnsupportedOperationException();
    }

    public static ArmyException unexpectedStmt(Stmt stmt) {
        return new ArmyException(String.format("Unexpected Stmt type[%s]", stmt));
    }


    public static TimeoutException timeout(int timeout, long overspendMills) {
        final long overspend = Math.abs(overspendMills);
        String m;
        m = String.format("timout[%s] seconds,but overspend %s millis", timeout, overspend);
        throw new TimeoutException(m, overspend);
    }

    public static ArmyException notSupportDialectMode(DialectMode dialectMode, ServerMeta serverMeta) {
        String m;
        m = String.format("%s isn't supported by %s", dialectMode, serverMeta);
        throw new ArmyException(m);
    }


}
