package io.army.util;

import io.army.ArmyException;
import io.army.DialectMode;
import io.army.criteria.CriteriaException;
import io.army.criteria.Statement;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
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

    public static CriteriaException unknownField(FieldMeta<?, ?> fieldMeta) {
        return new CriteriaException(String.format("Unknown %s", fieldMeta));
    }

    public static CriteriaException notMatchInsertField(_ValuesInsert insert, FieldMeta<?, ?> fieldMeta) {
        String m = String.format("Not match %s for %s in Statement %s", fieldMeta, insert.table(), insert);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownStatement(Statement stmt, GenericRmSessionFactory factory) {
        String m = String.format("Unknown %s in %s", stmt, factory);
        return new CriteriaException(m);
    }

    public static CriteriaException tableNotMatch(TableMeta<?> tableMeta1, TableMeta<?> tableMeta2) {
        String m = String.format("%s and %s not match", tableMeta1, tableMeta2);
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

    public static IllegalStateException nonPrepared(_Statement statement) {
        return new IllegalStateException(String.format("%s not prepared", statement));
    }

    public static CriteriaException noUpdateField(Update update) {
        return new CriteriaException(String.format("%s no any update field", update));
    }

    public static IllegalStateException updateFieldExpNotMatch() {
        return new IllegalStateException("Field and value expression count not match.");
    }

    public static CriteriaException immutableField(FieldMeta<?, ?> field) {
        return new CriteriaException(String.format("%s is immutable.", field));
    }

    public static CriteriaException nonInsertable(FieldMeta<?, ?> field) {
        return new CriteriaException(String.format("%s is non-insertable.", field));
    }

    public static CriteriaException unknownTableType(TableMeta<?> table) {
        return new CriteriaException(String.format("%s is unknown type.", table));
    }

    public static CriteriaException routeKeyValueError(TableMeta<?> table, int databaseIndex, int factoryDatabase) {
        String m = String.format("%s route database[%s] and primary statement database[%s] not match."
                , table, databaseIndex, factoryDatabase);
        return new CriteriaException(m);
    }

    public static CriteriaException notSupportSharding(TableMeta<?> table) {
        return new CriteriaException(String.format("%s not support sharding.", table));
    }

    public static CriteriaException notSupportDatabaseSharding(TableMeta<?> table) {
        return new CriteriaException(String.format("%s not support database sharding.", table));
    }

    public static CriteriaException notSupportTableSharding(TableMeta<?> table) {
        return new CriteriaException(String.format("%s not support table sharding.", table));
    }


}
