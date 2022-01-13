package io.army.util;

import io.army.ArmyException;
import io.army.DialectMode;
import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.meta.ServerMeta;
import io.army.meta.TableMeta;
import io.army.session.DialectSessionFactory;
import io.army.session.TimeoutException;
import io.army.sharding.DatabaseRoute;
import io.army.sharding.Route;
import io.army.sharding.RouteContext;
import io.army.stmt.Stmt;
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

    public static CriteriaException unknownColumn(QualifiedField<?, ?> field) {
        return new CriteriaException(String.format("Unknown %s", field));
    }

    public static CriteriaException unknownField(FieldMeta<?, ?> fieldMeta) {
        return new CriteriaException(String.format("Unknown %s", fieldMeta));
    }

    public static CriteriaException notMatchInsertField(_ValuesInsert insert, FieldMeta<?, ?> fieldMeta) {
        String m = String.format("Not match %s for %s in Statement %s", fieldMeta, insert.table(), insert);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownStatement(Statement stmt, Dialect dialect) {
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

    public static CriteriaException immutableField(FieldMeta<?, ?> field) {
        return new CriteriaException(String.format("%s is immutable.", field));
    }

    public static CriteriaException immutableTable(TableMeta<?> table) {
        return new CriteriaException(String.format("%s is immutable.", table));
    }

    public static CriteriaException armyManageField(FieldMeta<?, ?> field) {
        return new CriteriaException(String.format("%s is managed by Army.", field));
    }

    public static CriteriaException visibleFieldNoPredicate(GenericField<?, ?> field) {
        return new CriteriaException(String.format("%s couldn't present in where clause.", field));
    }

    public static CriteriaException insertExpDontSupportField(FieldMeta<?, ?> field) {
        String m = String.format("%s isn't supported by insert statement common expression.", field);
        return new CriteriaException(m);
    }

    public static CriteriaException nonNullField(FieldMeta<?, ?> field) {
        return new CriteriaException(String.format("%s is non-null.", field));
    }

    public static CriteriaException nonNullNamedParam(NonNullNamedParam<?> param) {
        String m = String.format("%s[%s] must be non-null.", NonNullNamedParam.class.getName(), param.name());
        return new CriteriaException(m);
    }

    public static CriteriaException nonInsertableField(FieldMeta<?, ?> field) {
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

    public static ArmyException tableIndexParseError(_Statement stmt, TableMeta<?> table, int tableIndex) {
        return new ArmyException(String.format("%s %s parsed table index[%s] and table count[%s] not match."
                , stmt, table, tableIndex, table.tableCount()));
    }

    public CriteriaException setClauseSizeError(_Update update) {
        String m = String.format("%s set clause target size[%s] and value size[%s] not match."
                , update, update.fieldList().size(), update.valueExpList().size());
        return new CriteriaException(m);
    }


    public static MetaException dontSupportOnlyDefault(Dialect dialect) {
        return new MetaException(String.format("%s isn't support UpdateMode[%s].", dialect, UpdateMode.ONLY_DEFAULT));
    }

    public static CriteriaException noWhereClause(_SqlContext context) {
        return new CriteriaException(String.format("%s no where clause.", context));
    }


    public static CriteriaException unknownSetTargetPart(SetTargetPart target) {
        return new CriteriaException(String.format("Unknown %s type[%s].", SetTargetPart.class.getName(), target));
    }

    public static CriteriaException unknownQueryType(Query query) {
        return new CriteriaException(String.format("unknown %s type.", query.getClass().getName()));
    }

    public static CriteriaException setTargetAndValuePartNotMatch(SetTargetPart target, SetValuePart value) {
        return new CriteriaException(String.format("%s[%s] and %s[%s] not match.", SetTargetPart.class.getName(), target
                , SetValuePart.class.getName(), value));
    }

    public static CriteriaException selfJoinNoLogicField(GenericField<?, ?> field) {
        return new CriteriaException(String.format("%s self join but don't use %s."
                , field.tableMeta(), QualifiedField.class.getName()));
    }

    public static CriteriaException javaTypeUnsupportedByMapping(MappingType type, Object nonNull) {
        return new CriteriaException(String.format("%s is unsupported by %s.", nonNull.getClass(), type.getClass()));
    }

    public static CriteriaException valueRouteAndNamedRouteConflict(_BatchDml batchDml
            , final byte tableIndex, FieldMeta<?, ?> routeField) {
        String m = String.format("Batch dml[%s] value table route[%s] and named table route field[%s] conflict."
                , batchDml.getClass().getName(), tableIndex, routeField);
        return new CriteriaException(m);
    }

    public static CriteriaException routeFieldIsNull(FieldMeta<?, ?> field, int batchIndex) {
        String m = String.format("Route field[%s] is null,batch index[%s],couldn't parse statement.", field, batchIndex);
        return new CriteriaException(m);
    }

    public static ArmyException routeFuncError(Route route, Object value) {
        String m = String.format("Table route %s parse value[%s] error.", route.getClass().getName(), value);
        return new ArmyException(m);
    }

    public static ArmyException notFoundDatabaseRouteFunc(FieldMeta<?, ?> field) {
        String m = String.format("Not found %s for %s", DatabaseRoute.class.getName(), field);
        return new ArmyException(m);
    }

    public static ArmyException predicateImplError(_Predicate predicate) {
        String m = String.format("The implementation of %s error.", predicate.getClass().getName());
        return new ArmyException(m);
    }

    public static CriteriaException multiDmlOnlySupportTable(_MultiDml dml, TablePart tablePart) {
        String m = String.format("Multi table dml only support %s.but present %s"
                , dml.getClass().getName(), tablePart.getClass().getName());
        return new CriteriaException(m);
    }

    public static CriteriaException databaseRouteAllFollow(_MultiDml dml) {
        String m = String.format("%s all follow primary route.", dml.getClass().getName());
        return new CriteriaException(m);
    }

    public static CriteriaException selectListIsEmpty() {
        return new CriteriaException("select list must not empty");
    }

    public static CriteriaException columnSubQuerySelectionError() {
        String m = String.format("%s selection size must equals one.", ColumnSubQuery.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException onClauseIsEmpty() {
        return new CriteriaException("on clause must not empty");
    }

    public static CriteriaException castCriteriaApi() {
        return new CriteriaException("You couldn't cast criteria api instance");
    }

    public static CriteriaException unknownSelectPart(SelectPart selectPart) {
        String m = String.format("unknown %s type[%s]", SelectPart.class.getName(), selectPart.getClass().getName());
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
        String m = String.format("From clause first %s must no join clause.", TablePart.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException firstTableHasOnClause() {
        String m = String.format("From clause first %s must no on clause.", TablePart.class.getName());
        return new CriteriaException(m);
    }


}
