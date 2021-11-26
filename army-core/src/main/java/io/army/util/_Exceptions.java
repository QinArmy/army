package io.army.util;

import io.army.ArmyException;
import io.army.DialectMode;
import io.army.criteria.CriteriaException;
import io.army.criteria.impl.inner._Statement;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.session.GenericRmSessionFactory;
import io.army.session.TimeoutException;
import io.army.stmt.Stmt;
import io.qinarmy.util.ExceptionUtils;

public abstract class _Exceptions extends ExceptionUtils {

    protected _Exceptions() {
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

    public static CriteriaException unknownTableAlias(String tableAlias) {
        String m = String.format("Unknown table alias[%s].", tableAlias);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownColumn(@Nullable String tableAlias, FieldMeta<?, ?> fieldMeta) {
        final String m;
        if (tableAlias == null) {
            m = String.format("Unknown column %s,%s", fieldMeta.columnName(), fieldMeta);
        } else {
            m = String.format("Unknown column %s.%s,%s", tableAlias, fieldMeta.columnName(), fieldMeta);
        }
        return new CriteriaException(m);
    }


    public static CriteriaException databaseRouteError(_Statement stmt, GenericRmSessionFactory factory) {
        String m = String.format("%s database route and %s not match.", stmt, factory);
        return new CriteriaException(m);
    }

    public static CriteriaException noTableRoute(_Statement stmt, GenericRmSessionFactory factory) {
        String m = String.format("Not found table route in %s.Factory %s", stmt, factory);
        return new CriteriaException(m);
    }


}
