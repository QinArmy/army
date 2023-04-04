package io.army.util;

import io.army.ArmyException;
import io.army.annotation.Generator;
import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.bean.ObjectAccessException;
import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBock;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect._SqlContext;
import io.army.env.ArmyKey;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.MappingTypeException;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
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


    public static MappingTypeException beforeBindMethod(SqlType sqlType, MappingType mappingType
            , @Nullable Object returnValue) {
        String m = String.format("%s beforeBind() method return type %s and %s type not match."
                , mappingType.getClass().getName(), _ClassUtils.safeClassName(returnValue), sqlType);
        return new MappingTypeException(m);
    }

    public static TransactionTimeOutException timeout(int timeout, long restMills) {
        String m;
        m = String.format("Expected completion in %s seconds,but rest %s millis", timeout, restMills);
        throw new TransactionTimeOutException(m);
    }

    public static MetaException dontSupportJavaType(FieldMeta<?> field, Class<?> javaType) {
        String m = String.format("%s don't support java type[%s]", field, javaType);
        throw new MetaException(m);
    }


    public static CriteriaException tableAliasDuplication(String tableAlias) {
        String m = String.format("Table alias[%s] duplication", tableAlias);
        return new CriteriaException(m);
    }

    public static CriteriaException nonStandardTableBlock(_TabularBock block) {
        String m = String.format("%s is non-standard %s", _ClassUtils.safeClassName(block), _TabularBock.class.getName());
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

    public static CriteriaException unsupportedFieldType(DataField field) {
        return new CriteriaException(String.format("unsupported field type %s", field));
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

    public static CriteriaException dontSupportTableItem(TabularItem item, String alias, @Nullable Dialect dialect) {
        final String dialectText;
        if (dialect == null) {
            dialectText = "standard statement";
        } else {
            dialectText = dialect.toString();
        }
        String m = String.format("%s don't support %s %s alias %s .",
                dialectText, TabularItem.class.getName(), _ClassUtils.safeClassName(item), alias);
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportModifier(SQLWords modifier, Dialect dialect) {
        String m = String.format("%s don't support modifier[%s]", dialect, modifier);
        return new CriteriaException(m);
    }

    public static CriteriaException valuesStatementDontSupportParam() {
        String m = "VALUES statement don't support parameter expression.";
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportLateralItem(TabularItem item, String alias, @Nullable Dialect dialect) {
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

    public static CriteriaException immutableTable(TabularItem tableItem) {
        return new CriteriaException(String.format("%s is immutable.", tableItem));
    }

    public static CriteriaException armyManageField(TableField field) {
        final String m;
        m = String.format("%s is managed by army,\nbelow is managed by army:\n%s,\n%s,\n%s,\n%s\n%s%s"
                , field

                , _MetaBridge.CREATE_TIME
                , _MetaBridge.UPDATE_TIME
                , _MetaBridge.VERSION
                , "discriminator"

                , "the field annotated by "
                , Generator.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException migrationModeGeneratorField(FieldMeta<?> field) {
        String m;
        m = String.format("%s is annotated by %s and non-null,so required in column list clause int migration mode."
                , field, Generator.class.getName());
        throw new CriteriaException(m);
    }

    public static CriteriaException migrationManageGeneratorField(FieldMeta<?> field) {
        String m;
        m = "reserved fields and discriminator required in column clause in migration mode,but not found %s";
        throw new CriteriaException(String.format(m, field));
    }

    public static CriteriaException childIdIsManagedByArmy(ChildTableMeta<?> table) {
        String m = String.format("child id %s always is managed by army", table.id());
        return new CriteriaException(m);
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

    public static CriteriaException identifierNoText() {
        return new CriteriaException("identifier must have text.");
    }

    public static CriteriaException identifierContainsDelimited(Database database, String identifier, char delimited) {
        String m = String.format("%s identifier[%s] couldn't contains delimited identifier[%s].",
                database.name(), identifier, delimited);
        return new CriteriaException(m);
    }

    public static CriteriaException objectNameContainsDelimited(Database database, DatabaseObject object, char delimited) {
        String m = String.format("%s DatabaseObject[%s] couldn't contains delimited identifier[%s].",
                database.name(), object, delimited);
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportAssignmentInsert(Dialect dialect) {
        String m = String.format("%s don't support assignment insert.", dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException nonNullField(DataField field) {
        return new CriteriaException(String.format("%s is non-null.", field));
    }

    public static ErrorChildInsertException parentDoNothingError(final _Insert._ChildInsert childStmt) {
        String m = String.format("parent %s of %s couldn't use DO NOTHING clause,because insert row count will error",
                childStmt.parentStmt().insertTable(),
                childStmt.insertTable());
        return new ErrorChildInsertException(m);
    }

    public static ErrorChildInsertException childDoNothingError(final _Insert._ChildInsert childStmt) {
        String m = String.format("child %s couldn't use DO NOTHING clause,because insert row count will error",
                childStmt.insertTable());
        return new ErrorChildInsertException(m);
    }

    public static ErrorChildInsertException forbidChildInsertSyntaxError(final _Insert._ChildInsert childStmt) {

        final ParentTableMeta<?> parentTable;
        parentTable = ((ChildTableMeta<?>) childStmt.insertTable()).parentMeta();

        String m = String.format("%s id %s is %s ,so you couldn't use ON DUPLICATE KEY clause(or ON CONFLICT clause).",
                parentTable, GeneratorType.class.getName(), parentTable.id().generatorType());
        return new ErrorChildInsertException(m);
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

    public static CriteriaException noFieldsForQueryInsert(TableMeta<?> table) {

        return new CriteriaException(String.format("No fields for query insert for %s", table));
    }

    public static CriteriaException rowSetSelectionAndFieldSizeNotMatch(int rowSetSelectionSize, int fieldSize
            , TableMeta<?> table) {
        String m = String.format("query selection size[%s] and field size[%s] not match for %s"
                , rowSetSelectionSize, fieldSize, table);
        return new CriteriaException(m);
    }

    public static CriteriaException childAndParentRowsNotMatch(ChildTableMeta<?> table, int parent, int child) {
        String m = String.format("%s rows number[%s] and parent row number[%s] not match"
                , table, child, parent);
        return new CriteriaException(m);
    }

    public static CriteriaException cteRefWithClauseOuterField(String cteName) {
        String m = String.format("Cte[%s] couldn't reference WITH clause outer field.", cteName);
        throw new CriteriaException(m);
    }

    public static CriteriaException duplicateKeyAndPostIdInsert(ChildTableMeta<?> table) {
        String m;
        m = String.format("%s don't support duplicate key clause or replace insert,because %s generator type is %s"
                , table, table.parentMeta().id(), GeneratorType.POST);
        return new CriteriaException(m);
    }

    public static CriteriaException multiStmtDontSupportPostParent(ChildTableMeta<?> childTable) {
        String m = String.format("multi-statement don't support %s with post parent", childTable);
        return new CriteriaException(m);
    }

    public static CriteriaException visibleFieldAndConflictClauseNotMatch(Dialect dialect, TableMeta<?> table) {
        String m = String.format("%s don't support conflict clause for non-%s mode,because %s exists %s field."
                , dialect, Visible.BOTH, table, _MetaBridge.VISIBLE);
        return new CriteriaException(m);
    }

    public static CriteriaException multiStmtDontSupportParam() {
        return new CriteriaException("multi-statement don't support parameter placeholder.");
    }

    public static CriteriaException notFondIdPredicate(Dialect dialect) {
        String m = String.format("%s update child table must specified id", dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportDialectStatement(DialectStatement statement, Dialect dialect) {
        String m = String.format("%s don't dialect statement[%s]", dialect, _ClassUtils.safeClassName(statement));
        return new CriteriaException(m);
    }

    public static CriteriaException nonUpdatableField(DataField field) {
        String m;
        m = String.format("%s %s isn't %s.", field, UpdateMode.class.getSimpleName(), UpdateMode.UPDATABLE);
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


    public static CriteriaException unknownRowSetType(RowSet query) {
        return new CriteriaException(String.format("unknown %s type.", query.getClass().getName()));
    }


    public static CriteriaException selfJoinNonQualifiedField(FieldMeta<?> field) {
        return new CriteriaException(String.format("%s self join but %s don't use %s."
                , field.tableMeta(), field, QualifiedField.class.getName()));
    }

    public static CriteriaException selectListIsEmpty() {
        return new CriteriaException("select list must not empty");
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

    public static CriteriaException cteNameNotText() {
        return new CriteriaException("Cte name must has text.");
    }

    public static CriteriaException queryInsertDontSupportLateralSubQuery() {
        return new CriteriaException("Query insert don't support lateral sub query.");
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

    public static CriteriaException nestedItemIsEmpty(_NestedItems nestedItems) {
        String m = String.format("%s %s is empty.", _NestedItems.class.getName(), _ClassUtils.safeClassName(nestedItems));
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
            m = String.format("%s must not empty.", _NestedItems.class.getName());
            e = new CriteriaException(m);
        } else {
            e = noFromClause();
        }
        return e;
    }

    public static CriteriaException standardDontSupportHint() {
        return new CriteriaException("Standard api don't support Hint");
    }

    public static CriteriaException dontSupportHint(Dialect dialect) {
        return new CriteriaException(String.format("%s don't support Hint", dialect));
    }

    public static CriteriaException dontSupportHint(Dialect dialect, Enum<?> hintType) {
        return new CriteriaException(String.format("%s don't support hint[%s]", dialect, hintType.name()));
    }

    public static CriteriaException returningListEmpty() {
        return new CriteriaException("returning dml no returning clause");
    }

    public static CriteriaException valueChildAndParentRowNumNotMatch(ChildTableMeta<?> table, int childRows, int parentRows) {
        String m = String.format("%s insert row number[%s] and parent insert row number[%s] not match"
                , table, childRows, parentRows);
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
        String m = String.format("Value[%s] out of %s.", nonNull, sqlType);
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

    public static CriteriaException unknownParamValue(@Nullable SQLParam paramValue) {
        String m = String.format("unknown %s type %s", SQLParam.class.getName()
                , _ClassUtils.safeClassName(paramValue));
        return new CriteriaException(m);
    }

    public static ArmyException unexpectedSqlParam(@Nullable SQLParam sqlParam) {
        String m;
        m = String.format("unexpected %s type %s", SQLParam.class.getName(), _ClassUtils.safeClassName(sqlParam));
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

    public static CriteriaException independentDmlDontSupportNamedValue() {
        String m = "Only the batch update(delete) in multi-statement context support named parameter(literal).";
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
                , resultType.getName(), selection.typeMeta().mappingType().getClass().getName());
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

    public static IllegalArgumentException tableDontBelongOf(TableMeta<?> table, SessionFactory sessionFactory) {
        String m = String.format("%s isn't belong of %s", table, sessionFactory);
        return new IllegalArgumentException(m);
    }

    public static SessionException dontSupportUniqueCache(SessionFactory sessionFactory) {
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


    public static IllegalArgumentException dialectDatabaseNotMatch(Dialect usedDialect, ServerMeta meta) {
        String m = String.format("ArmyKey %s %s database not match with server %s", ArmyKey.DIALECT, usedDialect, meta);
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException databaseNotCompatible(Dialect usedDialect, Database database) {
        String m = String.format("ArmyKey %s %s database not match with  %s", ArmyKey.DIALECT, usedDialect, database);
        return new IllegalArgumentException(m);
    }


    public static IllegalArgumentException dialectVersionNotCompatibility(Dialect dialect, ServerMeta meta) {
        String m = String.format("ArmyKey %s %s version not compatibility with server %s"
                , ArmyKey.DIALECT, dialect, meta);
        return new IllegalArgumentException(m);
    }


    public static CriteriaException nestedItemsAliasHasText(String alias) {
        String m = String.format("%s alias[%s] must be empty.", _NestedItems.class.getName(), alias);
        return new CriteriaException(m);
    }

    public static CriteriaException discriminatorError(ParentTableMeta<?> table, Enum<?> discriminator) {
        String m = String.format("%s.%s isn't discriminator of %s"
                , discriminator.getClass().getName(), discriminator.name(), table);
        return new CriteriaException(m);
    }

    public static CriteriaException tableItemAliasNoText(TabularItem tableItem) {
        String m = String.format("%s[%s] alias must be not empty."
                , TabularItem.class.getName(), _ClassUtils.safeClassName(tableItem));
        return new CriteriaException(m);
    }

    public static CriteriaException tableAliasIsEmpty() {
        return new CriteriaException("table alias must non-empty.");
    }

    public static ObjectAccessException nonWritableProperty(Object target, String propertyName) {
        String m = String.format("%s property[%s] isn't writable.", target, propertyName);
        return new ObjectAccessException(m);
    }

    public static ObjectAccessException propertyTypeNotMatch(FieldMeta<?> field, Object value) {
        String m = String.format("%s java type is %s,but value type is %s,not match."
                , field, field.javaType().getName(), _ClassUtils.safeClassName(value));
        return new ObjectAccessException(m);
    }

    public static ObjectAccessException nonReadableProperty(Object target, String propertyName) {
        String m = String.format("%s property[%s] isn't readable.", target, propertyName);
        return new ObjectAccessException(m);
    }


    public static MetaException discriminatorNoMapping(TableMeta<?> domainTable) {
        final FieldMeta<?> discriminator;
        discriminator = domainTable.discriminator();
        assert discriminator != null;
        Class<?> javaType;
        javaType = discriminator.javaType();
        if (javaType.isAnonymousClass()) {
            javaType = javaType.getSuperclass();
        }
        String m = String.format("%s code[%s] no mapping.", javaType.getName()
                , domainTable.discriminatorValue());
        throw new MetaException(m);
    }


}
