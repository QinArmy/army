package io.army.util;

import io.army.ArmyException;
import io.army.ReadOnlySessionException;
import io.army.SessionException;
import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner._Statement;
import io.army.criteria.impl.inner._Update;
import io.army.criteria.impl.inner._ValuesInsert;
import io.army.dialect.Dialect;
import io.army.dialect._Dialect;
import io.army.dialect._SqlContext;
import io.army.generator.PostFieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.session.*;
import io.army.sharding.RouteContext;
import io.army.sqltype.SqlType;
import io.army.stmt.Stmt;
import io.army.tx.ReadOnlyTransactionException;
import io.army.tx.TransactionException;
import io.army.tx.TransactionTimeOutException;
import io.qinarmy.util.ExceptionUtils;
import io.qinarmy.util.UnexpectedEnumException;


public abstract class _Exceptions extends ExceptionUtils {

    protected _Exceptions() {
        throw new UnsupportedOperationException();
    }

    public static UnexpectedEnumException unexpectedEnum(Enum<?> e) {
        return ExceptionUtils.createUnexpectedEnumException(e);
    }

    public static ArmyException unexpectedStmt(Stmt stmt) {
        return new ArmyException(String.format("Unexpected Stmt type[%s]", stmt));
    }

    public static ArmyException unexpectedStatement(Statement statement) {
        return new ArmyException(String.format("Unexpected %S type[%s]", Statement.class.getName(), statement));
    }


    public static TimeoutException timeout(int timeout, long overspendMills) {
        final long overspend = Math.abs(overspendMills);
        String m;
        m = String.format("timout[%s] seconds,but overspend %s millis", timeout, overspend);
        throw new TimeoutException(m, overspend);
    }

    public static ArmyException notSupportDialectMode(Dialect dialect, ServerMeta serverMeta) {
        String m;
        m = String.format("%s isn't supported by %s", dialect, serverMeta);
        throw new ArmyException(m);
    }

    public static CriteriaException unknownTableAlias(String tableAlias) {
        String m = String.format("Unknown table alias[%s].", tableAlias);
        return new CriteriaException(m);
    }

    public static CriteriaException tableAliasDuplication(String tableAlias) {
        String m = String.format("Table alias[%s] duplication", tableAlias);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownColumn(@Nullable String tableAlias, FieldMeta<?> fieldMeta) {
        final String m;
        if (tableAlias == null) {
            m = String.format("Unknown column %s,%s", fieldMeta.columnName(), fieldMeta);
        } else {
            m = String.format("Unknown column %s.%s,%s", tableAlias, fieldMeta.columnName(), fieldMeta);
        }
        return new CriteriaException(m);
    }

    public static CriteriaException unknownColumn(QualifiedField<?> field) {
        return new CriteriaException(String.format("Unknown %s", field));
    }

    public static CriteriaException unknownField(FieldMeta<?> fieldMeta) {
        return new CriteriaException(String.format("Unknown %s", fieldMeta));
    }

    public static CriteriaException notMatchInsertField(_ValuesInsert insert, FieldMeta<?> fieldMeta) {
        String m = String.format("Not match %s for %s in Statement %s", fieldMeta, insert.table(), insert);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownStatement(Statement stmt, _Dialect dialect) {
        String m = String.format("Unknown %s in %s", stmt.getClass().getName(), dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException tableNotMatch(TableMeta<?> tableMeta1, TableMeta<?> tableMeta2) {
        String m = String.format("%s and %s not match", tableMeta1, tableMeta2);
        return new CriteriaException(m);
    }


    public static CriteriaException databaseRouteError(_Statement stmt, DialectSessionFactory factory) {
        String m = String.format("%s database route and %s not match.", stmt, factory);
        return new CriteriaException(m);
    }

    public static ArmyException databaseRouteError(int databaseIndex, RouteContext factory) {
        String m = String.format("database index[%s] and Factory[%s] not match.", databaseIndex, factory);
        return new ArmyException(m);
    }

    public static CriteriaException noTableRoute(_Statement stmt, DialectSessionFactory factory) {
        String m = String.format("Not found table route in %s.Factory %s", stmt, factory);
        return new CriteriaException(m);
    }

    public static IllegalStateException nonPrepared(_Statement statement) {
        return new IllegalStateException(String.format("%s not prepared", statement));
    }

    public static CriteriaException noUpdateField(Update update) {
        return new CriteriaException(String.format("%s no any update field", update.getClass().getName()));
    }

    public static IllegalStateException updateFieldExpNotMatch() {
        return new IllegalStateException("Field and value expression count not match.");
    }

    public static CriteriaException immutableField(FieldMeta<?> field) {
        return new CriteriaException(String.format("%s is immutable.", field));
    }

    public static CriteriaException immutableTable(TableMeta<?> table) {
        return new CriteriaException(String.format("%s is immutable.", table));
    }

    public static CriteriaException armyManageField(FieldMeta<?> field) {
        return new CriteriaException(String.format("%s is managed by Army.", field));
    }

    public static CriteriaException visibleField(Visible visible, GenericField<?> field) {
        String m = String.format("%s mode is %s,%s couldn't present in non-selection expression."
                , Visible.class.getSimpleName(), visible, field);
        return new CriteriaException(m);
    }

    public static CriteriaException insertExpDontSupportField(FieldMeta<?> field) {
        String m = String.format("%s isn't supported by insert statement common expression.", field);
        return new CriteriaException(m);
    }

    public static CriteriaException nonNullField(FieldMeta<?> field) {
        return new CriteriaException(String.format("%s is non-null.", field));
    }

    public static CriteriaException nonNullExpression(FieldMeta<?> field) {
        return new CriteriaException(String.format("Value expression must be non-null for %s", field));
    }


    public static CriteriaException nonNullNamedParam(NonNullNamedParam param) {
        String m = String.format("%s[%s] must be non-null.", NonNullNamedParam.class.getName(), param.name());
        return new CriteriaException(m);
    }

    public static CriteriaException nonInsertableField(FieldMeta<?> field) {
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


    public static CriteriaException tableIndexAmbiguity(_Statement stmt, final int tableRoute
            , final int predicateTableIndex) {
        return new CriteriaException(String.format("%s table route[%s] and where clause table route[%s] ambiguity."
                , stmt.getClass().getName(), tableRoute, predicateTableIndex));
    }


    public CriteriaException setClauseSizeError(_Update update) {
        String m = String.format("%s set clause target size[%s] and value size[%s] not match."
                , update, update.fieldList().size(), update.valueExpList().size());
        return new CriteriaException(m);
    }


    public static MetaException dontSupportOnlyDefault(_Dialect dialect) {
        return new MetaException(String.format("%s isn't support UpdateMode[%s].", dialect, UpdateMode.ONLY_DEFAULT));
    }

    public static CriteriaException noWhereClause(_SqlContext context) {
        return new CriteriaException(String.format("%s no where clause.", context));
    }


    public static CriteriaException unknownSetTargetPart(SetLeftItem target) {
        return new CriteriaException(String.format("Unknown %s type[%s].", SetLeftItem.class.getName(), target));
    }

    public static CriteriaException unknownQueryType(Query query) {
        return new CriteriaException(String.format("unknown %s type.", query.getClass().getName()));
    }

    public static CriteriaException setTargetAndValuePartNotMatch(SetLeftItem target, SetRightItem value) {
        return new CriteriaException(String.format("%s[%s] and %s[%s] not match.", SetLeftItem.class.getName(), target
                , SetRightItem.class.getName(), value));
    }

    public static CriteriaException selfJoinNonQualifiedField(GenericField<?> field) {
        return new CriteriaException(String.format("%s self join but %s don't use %s."
                , field.tableMeta(), field, QualifiedField.class.getName()));
    }

    public static CriteriaException javaTypeUnsupportedByMapping(MappingType type, Object nonNull) {
        return new CriteriaException(String.format("%s is unsupported by %s.", nonNull.getClass(), type.getClass()));
    }


    public static CriteriaException selectListIsEmpty() {
        return new CriteriaException("select list must not empty");
    }

    public static CriteriaException ScalarSubQuerySelectionError() {
        String m = String.format("%s selection size must equals one.", ScalarSubQuery.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException onClauseIsEmpty() {
        return new CriteriaException("on clause must not empty");
    }

    public static CriteriaException castCriteriaApi() {
        return new CriteriaException("You couldn't cast criteria api instance");
    }

    public static CriteriaException unknownSelectItem(@Nullable SelectItem selectItem) {
        final String m;
        if (selectItem == null) {
            m = String.format("unknown %s type[null]", SelectItem.class.getName());
        } else {
            m = String.format("unknown %s type[%s]", SelectItem.class.getName(), selectItem.getClass().getName());
        }
        return new CriteriaException(m);
    }

    public static CriteriaException updateFieldListEmpty() {
        return new CriteriaException("Update statement field must not empty.");
    }

    public static CriteriaException fieldAndValueSizeNotMatch(int fieldSize, int valueSize) {
        String m = String.format("Update statement set clause fieldList size[%s] and valueList size[%s] not match."
                , fieldSize, valueSize);
        return new CriteriaException(m);
    }

    public static CriteriaException dmlNoWhereClause() {
        return new CriteriaException("Update/Delete statement no where clause,reject create.");
    }

    public static CriteriaException batchParamEmpty() {
        return new CriteriaException("Batch Update/Delete statement param list is empty.");
    }

    public static CriteriaException noFromClause() {
        return new CriteriaException("Not found from clause.");
    }

    public static CriteriaException firstTableHasJoinClause() {
        String m = String.format("From clause first %s must no join clause.", TableItem.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException firstTableHasOnClause() {
        String m = String.format("From clause first %s must no on clause.", TableItem.class.getName());
        return new CriteriaException(m);
    }

    public static ArmyException notServerVersion(ServerMeta meta) {
        String m = String.format("Currently,army don't support server[%s] yet.", meta);
        return new ArmyException(m);
    }

    public static CriteriaException outRangeOfSqlType(final SqlType sqlType, final Object nonNull) {
        String m = String.format("%s[%s] literal don't support java type[%s]"
                , sqlType.getClass().getName(), sqlType, nonNull.getClass().getName());
        return new CriteriaException(m);
    }

    public static CriteriaException outRangeOfSqlType(final SqlType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        String m = String.format("%s[%s] literal don't support java type[%s]"
                , sqlType.getClass().getName(), sqlType, nonNull.getClass().getName());
        final CriteriaException e;
        if (cause == null) {
            e = new CriteriaException(m);
        } else {
            e = new CriteriaException(m, cause);
        }
        return e;
    }

    public static CriteriaException valueOutRange(final SqlType sqlType, final Object nonNull) {
        return valueOutRange(sqlType, nonNull, null);
    }

    public static CriteriaException valueOutRange(final SqlType sqlType, final Object nonNull
            , @Nullable Throwable cause) {
        String m = String.format("Value[%s] out of %s.%s.", nonNull, sqlType.getClass().getName(), sqlType.name());
        final CriteriaException exception;
        if (cause == null) {
            exception = new CriteriaException(m);
        } else {
            exception = new CriteriaException(m, cause);
        }
        return exception;
    }

    public static CriteriaException operatorRightIsNullable(Enum<?> operator) {
        String m = String.format("Right expression of operator[%s] must be non-null.", operator);
        return new CriteriaException(m);
    }

    public static CriteriaException namedParamInNonBatch(NamedParam param) {
        String m = String.format("Couldn't exist named parameter[%s] in non-batch statement.", param.name());
        return new CriteriaException(m);
    }

    public static IllegalArgumentException stmtDontSupportDialect(Dialect mode) {
        return new IllegalArgumentException(String.format("Don't support dialect[%s]", mode));
    }


    public static SessionException readOnlySession(GenericSession session) {
        String m;
        m = String.format("%s of %s is read only,don't support DML.", session.sessionFactory(), session);
        return new ReadOnlySessionException(m);
    }

    public static SessionException readOnlyTransaction(GenericSession session) {
        String m;
        m = String.format("%s of %s in read only transaction,don't support DML.", session.sessionFactory(), session);
        return new ReadOnlyTransactionException(m);
    }

    public static SessionException sessionClosed(GenericSession session) {
        String m;
        m = String.format("%s of %s have closed.", session.sessionFactory(), session);
        return new SessionClosedException(m);
    }

    public static SessionException childDmlNoTransaction(GenericSession session, ChildTableMeta<?> table) {
        String m;
        m = String.format("%s of %s no transaction,so you don't execute dml about child table %s."
                , session.sessionFactory(), session, table);
        return new ChildDmlNoTractionException(m);
    }

    public static SessionException dontSupportNonVisible(GenericSession session, Visible visible) {
        String m;
        m = String.format("%s of %s don't support %s[%s]."
                , session.sessionFactory(), session, Visible.class.getName(), visible);
        return new NotSupportNonVisibleException(m);
    }

    public static SessionException dontSupportSubQueryInsert(GenericSession session) {
        String m;
        m = String.format("%s of %s don't support sub query insert."
                , session.sessionFactory(), session);
        return new NotSupportNonVisibleException(m);
    }


    public static MetaException autoIdErrorJavaType(PrimaryFieldMeta<?> field) {
        String m;
        m = String.format("%s don't support java type[%s],%s", PostFieldGenerator.class.getName()
                , field.javaType().getName(), field);
        throw new MetaException(m);
    }

    public static TransactionException transactionTimeout(long resetMillis) {
        return new TransactionTimeOutException(String.format("transaction timeout,reset %s", resetMillis));
    }


}
