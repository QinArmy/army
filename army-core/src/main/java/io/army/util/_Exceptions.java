package io.army.util;

import io.army.ArmyException;
import io.army.annotation.Generator;
import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.bean.ObjectAccessException;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.env.ArmyKey;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.session.*;
import io.army.sqltype.SqlType;
import io.army.stmt.Stmt;
import io.army.tx.ReadOnlyTransactionException;
import io.army.tx.TransactionTimeOutException;
import io.qinarmy.util.ExceptionUtils;
import io.qinarmy.util.UnexpectedEnumException;

import java.util.Collection;
import java.util.List;


public abstract class _Exceptions extends ExceptionUtils {

    protected _Exceptions() {
        throw new UnsupportedOperationException();
    }

    public static UnexpectedEnumException unexpectedEnum(Enum<?> e) {
        return ExceptionUtils.createUnexpectedEnumException(e);
    }

    public static ArmyException unknownError(String message, RuntimeException e) {
        return new ArmyException(message, e);
    }

    public static ArmyException unexpectedStmt(Stmt stmt) {
        return new ArmyException(String.format("Unexpected Stmt type[%s]", stmt));
    }

    public static ArmyException unexpectedStatement(Statement statement) {
        return new ArmyException(String.format("Unexpected %s type[%s]", Statement.class.getName(), statement));
    }


    public static TransactionTimeOutException timeout(int timeout, long restMills) {
        String m;
        m = String.format("Expected completion in %s seconds,but rest %s millis", timeout, restMills);
        throw new TransactionTimeOutException(m);
    }

    public static CriteriaException tableAliasDuplication(String tableAlias) {
        String m = String.format("Table alias[%s] duplication", tableAlias);
        return new CriteriaException(m);
    }

    public static CriteriaException nonStandardTableBlock(_TableBlock block) {
        String m = String.format("%s is non-standard %s", _ClassUtils.safeClassName(block), _TableBlock.class.getName());
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

    public static CriteriaException unknownColumn(DataField field) {
        return new CriteriaException(String.format("Unknown %s", field));
    }

    public static CriteriaException unknownTableAlias(String alias) {
        return new CriteriaException(String.format("Unknown table alias %s", alias));
    }

    public static CriteriaException unknownTable(TableMeta<?> table, String alias) {
        return new CriteriaException(String.format("Unknown table %s %s ", table, alias));
    }

    public static CriteriaException tableSelfJoin(TableMeta<?> table) {
        return new CriteriaException(String.format("%s self-join", table));
    }

    public static CriteriaException dontSupportTableItem(TableItem item, String alias) {
        String m = String.format("Don't support %s %s alias %s .",
                TableItem.class.getName(), _ClassUtils.safeClassName(item), alias);
        return new CriteriaException(m);
    }


    public static CriteriaException dontSupportLateralItem(TableItem item, String alias, @Nullable Dialect dialect) {
        String m = String.format("%s Don't support LATERAL %s alias %s ."
                , dialect == null ? "Standard" : dialect, _ClassUtils.safeClassName(item), alias);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownStatement(Statement stmt, Dialect dialect) {
        String m = String.format("Unknown %s in %s", stmt.getClass().getName(), dialect);
        return new CriteriaException(m);
    }


    public static CriteriaException immutableField(DataField field) {
        return new CriteriaException(String.format("%s is immutable.", field));
    }

    public static CriteriaException immutableTable(TableItem tableItem) {
        return new CriteriaException(String.format("%s is immutable.", tableItem));
    }

    public static CriteriaException armyManageField(TableField field) {
        return new CriteriaException(String.format("%s is managed by Army.", field));
    }

    public static CriteriaException visibleField(Visible visible, TableField field) {
        String m = String.format("%s mode is %s,%s couldn't present in non-selection expression."
                , Visible.class.getSimpleName(), visible, field);
        return new CriteriaException(m);
    }

    public static CriteriaException insertExpDontSupportField(FieldMeta<?> field) {
        String m = String.format("%s isn't supported by insert statement common expression.", field);
        return new CriteriaException(m);
    }

    public static CriteriaException nonNullField(TableField field) {
        return new CriteriaException(String.format("%s is non-null.", field));
    }

    public static CriteriaException updateChildFieldWithSingleUpdate(ChildTableMeta<?> table) {
        String m = String.format("%s is %s,you can only update parent table field in single update syntax."
                , table, ChildTableMeta.class.getName());
        return new CriteriaException(m);
    }

    public static ArmyException generatorFieldIsNull(FieldMeta<?> field) {
        return new ArmyException(String.format("%s has generator but value is null.", field));
    }


    public static CriteriaException nonNullNamedParam(NamedParam param) {
        String m = String.format("%s[%s] must be non-null.", NamedParam.class.getName(), param.name());
        return new CriteriaException(m);
    }

    public static CriteriaException nonInsertableField(TableField field) {
        return new CriteriaException(String.format("%s is non-insertable.", field));
    }

    public static CriteriaException noFieldsForRowSetInsert(TableMeta<?> table) {

        return new CriteriaException(String.format("No fields for row set insert for %s", table));
    }

    public static CriteriaException rowSetSelectionAndFieldSizeNotMatch(int rowSetSelectionSize, int fieldSize
            , TableMeta<?> table) {
        String m = String.format("RowSet selection size[%s] and field size[%s] not match for %s"
                , rowSetSelectionSize, fieldSize, table);
        return new CriteriaException(m);
    }

    public static CriteriaException duplicateKeyAndPostIdInsert(ChildTableMeta<?> table) {
        String m;
        m = String.format("%s don't support duplicate key clause or replace insert,because %s generator type is %s"
                , table, table.parentMeta().id(), GeneratorType.POST);
        return new CriteriaException(m);
    }

    public static MetaException dontSupportOnlyDefault(Dialect dialect) {
        return new MetaException(String.format("%s isn't support UpdateMode[%s].", dialect, UpdateMode.ONLY_DEFAULT));
    }

    public static CriteriaException setClauseNotExists() {
        return new CriteriaException("Not found SET clause,please SET clause");
    }

    public static CriteriaException existsChildFieldInSetClause(SingleTableMeta<?> table) {
        String m = String.format("%s present unknown field in SET clause.", table);
        return new CriteriaException(m);
    }

    public static CriteriaException existsChildFieldInMultiTableSetClause() {
        return new CriteriaException("Exists child filed in multi-table SET clause.");
    }

    public static CriteriaException noWhereClause(_SqlContext context) {
        return new CriteriaException(String.format("%s no where clause.", context));
    }

    public static CriteriaException expressionIsNull() {
        String m = String.format("expression must be non-null,if you want to output NULL,than use %s.%s or %s.%s."
                , SQLs.class.getName(), "nullWord()", SQLs.class.getName(), "nullParam(ParamMeta)");
        return new CriteriaException(m);
    }

    public static CriteriaException deleteChildButNoParent(ChildTableMeta<?> child, String alias) {
        String m = String.format("You delete %s but no %s.", child, child.parentMeta());
        return new CriteriaException(m);
    }

    public static CriteriaException unknownSetTargetPart(SetLeftItem target) {
        return new CriteriaException(String.format("Unknown %s type[%s].", SetLeftItem.class.getName(), target));
    }

    public static CriteriaException unknownRowSetType(RowSet query) {
        return new CriteriaException(String.format("unknown %s type.", query.getClass().getName()));
    }

    public static CriteriaException setTargetAndValuePartNotMatch(SetLeftItem target, SetRightItem value) {
        return new CriteriaException(String.format("%s[%s] and %s[%s] not match.", SetLeftItem.class.getName(), target
                , SetRightItem.class.getName(), value));
    }

    public static CriteriaException selfJoinNonQualifiedField(FieldMeta<?> field) {
        return new CriteriaException(String.format("%s self join but %s don't use %s."
                , field.tableMeta(), field, QualifiedField.class.getName()));
    }

    public static CriteriaException selectListIsEmpty() {
        return new CriteriaException("select list must not empty");
    }

    public static CriteriaException ScalarSubQuerySelectionError() {
        String m = String.format("%s selection size must equals one.", ScalarSubQuery.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException castCriteriaApi() {
        return new CriteriaException("You couldn't cast criteria api instance");
    }

    public static CriteriaException namedWindowNoText() {
        return new CriteriaException("named window must has text name.");
    }

    public static CriteriaException dontSupportWithClause(Dialect dialect) {
        return new CriteriaException(String.format("%s don't support WITH clause", dialect));
    }

    public static CriteriaException windowNotExists(@Nullable String windowName) {
        return new CriteriaException(String.format("Window[name : %s] not exists.", windowName));
    }

    public static CriteriaException windowListIsEmpty() {
        return new CriteriaException("window list must not empty.");
    }


    public static CriteriaException havingIsEmpty() {
        return new CriteriaException("having predicate list must not empty.");
    }

    public static CriteriaException cteListIsEmpty() {
        return new CriteriaException("with clause cte list must not empty. ");
    }

    public static CriteriaException predicateListIsEmpty() {
        return new CriteriaException("predicate list must not empty.");
    }

    public static CriteriaException joinTypeNoOnClause(_JoinType joinType) {
        return new CriteriaException(String.format("%s no ON clause.", joinType));
    }

    public static CriteriaException dontSupportJoinType(_JoinType joinType, Dialect dialect) {
        return new CriteriaException(String.format("%s don't support %s", dialect, joinType));
    }

    public static CriteriaException nestedItemIsEmpty(NestedItems nestedItems) {
        String m = String.format("%s %s is empty.", NestedItems.class.getName(), _ClassUtils.safeClassName(nestedItems));
        return new CriteriaException(m);
    }

    public static CriteriaException sortItemListIsEmpty() {
        return new CriteriaException("sortItem list must not empty.");
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

    public static CriteriaException valuesColumnSizeNotMatch(int firstRowColumnSize, int rowNum, int columnSize) {
        String m = String.format("Values number %s row column size[%s] and first row column size[%s] not match"
                , rowNum, columnSize, firstRowColumnSize);
        return new CriteriaException(m);
    }

    public static CriteriaException updateFieldListEmpty() {
        return new CriteriaException("Update statement field must not empty.");
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

    public static CriteriaException standardLimitClauseError(final long offset, final long rowCount) {
        String m;
        m = String.format("standard api limit clause offset[%s] non-negative so rowCount[%s] must non-negative"
                , offset, rowCount);
        throw new CriteriaException(m);

    }


    public static CriteriaException lateralSubQueryErrorPosition() {
        return new CriteriaException("LATERAL sub query present in error position");
    }

    public static CriteriaException tableBlockListIsEmpty(boolean nested) {
        final CriteriaException e;
        if (nested) {
            final String m;
            m = String.format("%s must not empty.", NestedItems.class.getName());
            e = new CriteriaException(m);
        } else {
            e = noFromClause();
        }
        return e;
    }

    public static CriteriaException standardDontSupportHint() {
        return new CriteriaException("Standard api don't Hint");
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

    public static CriteriaException namedParamInNonBatch() {
        return new CriteriaException("Couldn't exist named parameter in non-batch statement.");
    }

    public static CriteriaException invalidNamedParam(String name) {
        return new CriteriaException(String.format("Invalidate named parameter[%s]", name));
    }

    public static CriteriaException namedParamInNonBatch(NamedParam namedParam) {
        String m = String.format("Couldn't exist %s[%s] in non-batch statement."
                , NamedParam.class.getName(), namedParam.name());
        return new CriteriaException(m);
    }

    public static CriteriaException noNamedParamInBatch() {
        return new CriteriaException("Not found named parameter in batch statement.");
    }

    public static CriteriaException namedParamsInNonBatch(NamedParam.NamedMulti param) {
        String m = String.format("Couldn't exist named parameters[name:%s,size:%s] in non-batch statement."
                , param.name(), param.valueSize());
        return new CriteriaException(m);
    }

    public static CriteriaException namedParamNotMatch(SqlValueParam.NamedMultiValue param, @Nullable Object value) {
        String m = String.format("named value[name:%s,size:%s] value[%s] isn't %s."
                , param.name(), param.valueSize(), _ClassUtils.safeClassName(value), Collection.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException namedMultiParamSizeError(SqlValueParam.NamedMultiValue param, int size) {
        String m = String.format("named collection parameters[name:%s,size:%s] value size[%s] error."
                , param.name(), param.valueSize(), size);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownParamValue(@Nullable SqlParam paramValue) {
        String m = String.format("unknown %s type %s", SqlParam.class.getName()
                , _ClassUtils.safeClassName(paramValue));
        return new CriteriaException(m);
    }

    public static ArmyException unexpectedSqlParam(@Nullable SqlParam sqlParam) {
        String m;
        m = String.format("unexpected %s type %s", SqlParam.class.getName(), _ClassUtils.safeClassName(sqlParam));
        return new ArmyException(m);
    }

    public static CriteriaException namedParamErrorPosition(String name) {
        String m = String.format("named parameter[%s] present in error position,", name);
        return new CriteriaException(m);
    }


    public static CriteriaException nonScalarSubQuery(SubQuery subQuery) {
        String m = String.format("Expression right value[%s] is non-scalar sub query.", subQuery.getClass().getName());
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportRowLeftItem(@Nullable Dialect dialect) {
        String m = String.format("%s don't support ROW in SET clause.", dialect == null ? "standard" : dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException singleUpdateChildField(TableField field, Dialect dialect) {
        String m;
        m = String.format("%s single table update syntax don't support update child table field %s", dialect, field);
        return new CriteriaException(m);
    }

    public static CriteriaException dialectAndModifierNotMatch(Dialect dialect, SQLWords modifier) {
        String m = String.format("%s and %s not match.", dialect, modifier);
        return new CriteriaException(m);
    }

    public static CriteriaException commandAndModifierNotMatch(_DialectStatement statement, SQLWords modifier) {
        String m = String.format("%s and %s not match.", statement.getClass().getName(), modifier);
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
        String m = String.format("%s %s %s type don't support java type %s"
                , field, Generator.class.getName(), GeneratorType.POST, field.javaType().getName());
        return new MetaException(m);
    }


    public static ArmyException expectedTypeAndResultNotMatch(Selection selection, Class<?> resultType) {
        String m = String.format("expected type %s and query result mapping type %s not match."
                , resultType.getName(), selection.paramMeta().mappingType().getClass().getName());
        throw new ArmyException(m);
    }

    public static OptimisticLockException optimisticLock(long affectedRows) {
        String m = String.format("Affected rows is %s,don't satisfy expected rows.", affectedRows);
        return new OptimisticLockException(m);
    }

    public static OptimisticLockException batchOptimisticLock(int batchIndex, long affectedRows) {
        String m = String.format("Batch index[%s] affected rows is %s,don't satisfy expected rows."
                , batchIndex, affectedRows);
        return new OptimisticLockException(m);
    }

    public static DataAccessException batchCountNotMatch(int paramGroupCount, int batchResultCount) {
        String m = String.format("Parameter group count[%s] but batch result count is %s,not match."
                , paramGroupCount, batchResultCount);
        return new DataAccessException(m);
    }

    public static IllegalArgumentException tableDontBelongOf(TableMeta<?> table, GenericSessionFactory sessionFactory) {
        String m = String.format("%s isn't belong of %s", table, sessionFactory);
        return new IllegalArgumentException(m);
    }

    public static SessionException dontSupportUniqueCache(GenericSessionFactory sessionFactory) {
        String m = String.format("%s don't support unique cache,because config %s is %s."
                , sessionFactory, ArmyKey.DDL_MODE.name, DdlMode.NONE);
        return new SessionUsageException(m);
    }


    public static NotMatchRowException notMatchRow(GenericSession session, TableMeta<?> table, Object id) {
        String m = String.format("%s update failure,not found match row for %s and id %s.", session, table, id);
        return new NotMatchRowException(m);
    }

    public static NonUniqueException nonUnique(List<?> list) {
        String m = String.format("select result[%s] more than 1.", list.size());
        return new NonUniqueException(m);
    }


    public static IllegalArgumentException dialectDatabaseNotMatch(Dialect dialect, ServerMeta meta) {
        String m = String.format("ArmyKey %s %s database not match with server %s", ArmyKey.DIALECT, dialect, meta);
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException dialectVersionNotCompatibility(Dialect dialect, ServerMeta meta) {
        String m = String.format("ArmyKey %s %s version not compatibility with server %s"
                , ArmyKey.DIALECT, dialect, meta);
        return new IllegalArgumentException(m);
    }


    public static CriteriaException nestedItemsAliasHasText(String alias) {
        String m = String.format("%s alias[%s] must be empty.", NestedItems.class.getName(), alias);
        return new CriteriaException(m);
    }

    public static CriteriaException tableItemAliasNoText(TableItem tableItem) {
        String m = String.format("%s[%s] alias must be not empty."
                , TableItem.class.getName(), _ClassUtils.safeClassName(tableItem));
        return new CriteriaException(m);
    }

    public static ObjectAccessException nonWritableProperty(Object target, String propertyName) {
        String m = String.format("%s property[%s] isn't writable.", target, propertyName);
        return new ObjectAccessException(m);
    }

    public static ObjectAccessException nonReadableProperty(Object target, String propertyName) {
        String m = String.format("%s property[%s] isn't readable.", target, propertyName);
        return new ObjectAccessException(m);
    }


}
