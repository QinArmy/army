/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.util;

import io.army.ArmyException;
import io.army.annotation.Generator;
import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.bean.ObjectAccessException;
import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._JoinType;
import io.army.criteria.impl.inner._AliasDerivedBlock;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.impl.inner._NestedItems;
import io.army.criteria.impl.inner._TabularBlock;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.env.ArmyKey;
import io.army.env.EscapeMode;
import io.army.executor.DataAccessException;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.option.Option;
import io.army.result.*;
import io.army.session.*;
import io.army.spec.OptionSpec;
import io.army.sqltype.DataType;
import io.army.sqltype.SQLType;
import io.army.stmt.MultiStmt;
import io.army.stmt.Stmt;
import io.army.transaction.*;
import io.army.type.SqlRecord;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public abstract class _Exceptions {

    private _Exceptions() {
        throw new UnsupportedOperationException();
    }

    public static final Supplier<NullPointerException> NO_ANY_ROW_FUNC = _Exceptions::noAnyRow;

    public static ArmyException unexpectedEnum(Enum<?> e) {
        String m = String.format("unexpected enum %s.%s", e.getClass().getName(), e.name());
        return new ArmyException(m);
    }

    public static Throwable wrapIfNeed(final Throwable cause) {
        final Throwable error;
        if (!(cause instanceof Exception)) {
            error = cause;
        } else if (cause instanceof ArmyException) {
            error = cause;
        } else {
            error = unknownError(cause);
        }
        return error;
    }

    public static ArmyException unknownError(Throwable e) {
        return new ArmyException("unknown error," + e.getMessage(), e);
    }

    public static ArmyException unknownError(String message, Throwable e) {
        return new ArmyException(message, e);
    }

    public static SessionException unknownSessionError(Session session, Throwable clause) {
        String m = String.format("session[%s]\n occur unknown error,", session.name());
        return new SessionException(m, clause);
    }

    public static ArmyException unexpectedStmt(Stmt stmt) {
        return new ArmyException(String.format("Unexpected Stmt type[%s]", stmt));
    }

    public static ArmyException unexpectedStatement(Statement statement) {
        return new ArmyException(String.format("Unexpected %s type[%s]", Statement.class.getName(), statement));
    }


    public static MetaException beforeBindMethod(DataType sqlType, MappingType mappingType,
                                                 @Nullable Object returnValue) {
        String m = String.format("%s beforeBind() method return type %s and %s type not match."
                , mappingType, ClassUtils.safeClassName(returnValue), sqlType);
        return new MetaException(m);
    }

    public static CriteriaException voidClassSupportedByProcedure() {
        return new CriteriaException("void.class is supported by procedure.");
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

    public static CriteriaException nonStandardTableBlock(_TabularBlock block) {
        String m = String.format("%s is non-standard %s", ClassUtils.safeClassName(block), _TabularBlock.class.getName());
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


    public static CriteriaException unknownColumn(SqlField field) {
        return new CriteriaException(String.format("Unknown %s", field));
    }

    public static CriteriaException targetTableFiledAsInsertValue(TableField field) {
        return new CriteriaException(String.format("%s couldn't be insert value", field));
    }

    public static CriteriaException unsupportedFieldType(SqlField field) {
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
                dialectText, TabularItem.class.getName(), ClassUtils.safeClassName(item), alias);
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
                , dialect == null ? "Standard" : dialect, ClassUtils.safeClassName(item), alias);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownStatement(Statement stmt, Dialect dialect) {
        String m = String.format("Unknown %s in %s", stmt.getClass().getName(), dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException cursorNameNoText() {
        return new CriteriaException("cursor name must have text.");
    }

    public static CriteriaException varNameNoText() {
        return new CriteriaException("Variables name must have text.");
    }

    public static CriteriaException dontSupportVariableExpression(Database database) {
        String m = String.format("%s don't support variable expression", database);
        return new CriteriaException(m);
    }


    public static CriteriaException immutableField(SqlField field) {
        return new CriteriaException(String.format("%s is immutable.", field));
    }

    public static CriteriaException immutableTable(TabularItem tableItem) {
        return new CriteriaException(String.format("%s is immutable.", tableItem));
    }

    public static IllegalArgumentException cursorDirectionNotOneRow(Direction direction) {
        String m = String.format("cursor direction[%s] isn't onw row", direction.name());
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException cursorDirectionNoRowCount(Direction direction) {
        String m = String.format("cursor direction[%s] must be used with row count", direction.name());
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException cursorDirectionDontSupportRowCount(Direction direction) {
        String m = String.format("cursor direction[%s] don't support row count", direction.name());
        return new IllegalArgumentException(m);
    }

    public static NullPointerException noAnyRow() {
        return new NullPointerException("expected one row,but nothing");
    }

    public static DataAccessException cursorHaveClosed(String name) {
        String m = String.format("cursor[%s] have closed", name);
        return new DataAccessException(m);
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

    public static CriteriaException userVariableFirstCharIsAt(String varName) {
        String m = String.format("user variable[%s] first char couldn't be  '@' ", varName);
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

    public static CriteriaException nonNullField(SqlField field) {
        return new CriteriaException(String.format("%s is non-null.", field));
    }

    public static ErrorChildInsertException parentDoNothingError(final _Insert._ChildInsert childStmt) {
        String m = String.format("parent %s of %s couldn't use DO NOTHING clause,because insert row count will error",
                childStmt.parentStmt().table(),
                childStmt.table());
        return new ErrorChildInsertException(m);
    }

    public static ErrorChildInsertException childDoNothingError(final _Insert._ChildInsert childStmt) {
        String m = String.format("child %s couldn't use DO NOTHING clause,because insert row count will error",
                childStmt.table());
        return new ErrorChildInsertException(m);
    }

    public static ErrorChildInsertException forbidChildInsertSyntaxError(final _Insert._ChildInsert childStmt) {

        final ParentTableMeta<?> parentTable;
        parentTable = ((ChildTableMeta<?>) childStmt.table()).parentMeta();

        String m = String.format("%s id %s is %s ,so you couldn't use ON DUPLICATE KEY clause(or ON CONFLICT clause).",
                parentTable, GeneratorType.class.getName(), parentTable.id().generatorType());
        return new ErrorChildInsertException(m);
    }

    public static ArmyException literalEscapeModeError(EscapeMode mode, Dialect dialect) {
        String m = String.format("%s don't support %s for %s", dialect, mode, ArmyKey.LITERAL_ESCAPE_MODE.name);
        return new ArmyException(m);
    }

    public static ArmyException identifierEscapeModeError(EscapeMode mode, Dialect dialect) {
        String m = String.format("%s don't support %s for %s", dialect, mode, ArmyKey.IDENTIFIER_ESCAPE_MODE.name);
        return new ArmyException(m);
    }


    public static CriteriaException dontSupportNullMode(NullMode mode, Dialect dialect) {
        return new CriteriaException(String.format("%s don't support %s", mode, dialect));
    }

    public static CriteriaException nonNullNamedParam(NamedParam param) {
        String m = String.format("%s[%s] must be non-null.", NamedParam.class.getName(), param.name());
        return new CriteriaException(m);
    }

    public static CriteriaException nonInsertableField(TableField field) {
        return new CriteriaException(String.format("%s is non-insertable.", field));
    }


    public static CriteriaException duplicateKeyAndPostIdInsert(ChildTableMeta<?> table) {
        String m;
        m = String.format("insert multi-row , %s don't support duplicate key clause or replace insert,because %s generator type is %s, so database couldn't return correct multi parent ids",
                table, table.parentMeta().id(), GeneratorType.POST);
        return new CriteriaException(m);
    }

    public static IllegalArgumentException arrayElementError() {
        return new IllegalArgumentException("array element error");
    }

    public static CriteriaException illegalExpression(Expression expression) {
        String m = String.format("error,illegal expression %s", expression);
        return new CriteriaException(m);
    }


    public static CriteriaException cannotReturnPostId(final _Insert domainStmt) {
        final String tip;
        final _Insert nonChildStmt;
        final Function<String, CriteriaException> function;
        if (domainStmt instanceof _Insert._ChildInsert) {
            nonChildStmt = ((_Insert._ChildInsert) domainStmt).parentStmt();
            tip = "";
            function = ErrorChildInsertException::new;
        } else {
            nonChildStmt = domainStmt;
            tip = "If domain insert mode and single table,you should use ignoreReturnIds insert option";
            function = CriteriaException::new;
        }
        final TableMeta<?> insertTable = nonChildStmt.table();
        final PrimaryFieldMeta<?> idField = insertTable.id();
        final String f;
        f = "%s %s is %s and insert multi row and exists ignorable conflict clause,so database couldn't return multi ids. %s";
        String m = String.format(f, idField, GeneratorType.class.getName(), GeneratorType.POST, tip);

        return function.apply(m);
    }

    public static IllegalTwoStmtModeException illegalTwoStmtMode() {
        return new IllegalTwoStmtModeException("illegal two statement mode,first statement exists returning clause and second statement not exists returning clause.");
    }

    public static CriteriaException conflictClauseAndVisibleNotMatch(Dialect dialect, _Insert stmt, final Visible visible) {
        final StringBuilder builder = new StringBuilder();
        if (stmt instanceof _Insert._ChildInsert) {
            builder.append(((_Insert._ChildInsert) stmt).parentStmt().table());
        } else {
            builder.append(stmt.table());
        }
        builder.append(" contain ")
                .append(_MetaBridge.VISIBLE)
                .append(" field")
                .append(_Constant.COMMA)
                .append(dialect.name())
                .append(" conflict clause possibly update ");

        switch (visible) {
            case ONLY_VISIBLE:
                builder.append(Visible.ONLY_NON_VISIBLE.name());
                break;
            case ONLY_NON_VISIBLE:
                builder.append(Visible.ONLY_VISIBLE.name());
                break;
            default:
                //no bug,never here
                throw _Exceptions.unexpectedEnum(visible);
        }
        builder.append(" row,because current visible mode is ")
                .append(visible);
        return new CriteriaException(builder.toString());
    }

    public static ErrorChildInsertException doNothingConflict(final _Insert._ChildInsert childStmt) {
        final StringBuilder builder = _StringUtils.builder();
        builder.append("couldn't insert ")
                .append(childStmt.table())
                .append(",because insert multi-row and ")
                .append("exists conflict clause with DO NOTHING")
                .append(",child insert row count and parent insert row count possibly not match");
        return new ErrorChildInsertException(builder.toString());
    }

    public static CriteriaException multiStmtDontSupportParam() {
        return new CriteriaException("multi-statement don't support parameter placeholder.");
    }

    public static CriteriaException multiStmtForBatchRequiredNamedLiteral() {
        return new CriteriaException("multi-statement for batch must exists named literal");
    }

    public static CriteriaException notFondIdPredicate(Dialect dialect) {
        String m = String.format("%s update child table must specified id", dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException derivedColumnAliasSizeNotMatch(_AliasDerivedBlock block) {
        String m = String.format("derived table[%s] column alias list size[%s] and selection list size[%s] not match.",
                block.alias(), block.columnAliasList().size(), block.refAllSelection().size());
        return new CriteriaException(m);
    }

    public static CriteriaException dontSupportDialectStatement(DialectStatement statement, Dialect dialect) {
        String m = String.format("%s don't dialect statement[%s]", dialect, ClassUtils.safeClassName(statement));
        return new CriteriaException(m);
    }

    public static CriteriaException nonUpdatableField(SqlField field) {
        String m;
        m = String.format("%s %s isn't %s.", field, UpdateMode.class.getSimpleName(), UpdateMode.UPDATABLE);
        return new CriteriaException(m);
    }

    public static DataAccessException batchUpdateReturnResultSet() {
        return new DataAccessException("error,multi-statement batch update return ResultSet");
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

    public static CriteriaException literalDontSupport(SQLType sqlType) {
        return new CriteriaException(String.format("literal don't support %s", sqlType));
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

    public static CriteriaException dontSupportForUpdateClause(Dialect dialect) {
        return new CriteriaException(String.format("%s don't support FOR UPDATE clause", dialect));
    }

    public static CriteriaException dontSupportWithClauseInInsert(Dialect dialect) {
        String m = String.format("%s don't support WITH clause in INSERT statement.", dialect);
        return new CriteriaException(m);
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
        String m = String.format("%s %s is empty.", _NestedItems.class.getName(), ClassUtils.safeClassName(nestedItems));
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

    public static IllegalArgumentException chainAndReleaseConflict() {
        String m = String.format("%s[true] and %s[true] conflict", Option.CHAIN.name(), Option.RELEASE.name());
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException dontSupportRelease(Database database) {
        String m = String.format("%s don't support %s", database.name(), Option.RELEASE);
        return new IllegalArgumentException(m);
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

    public static CriteriaException outRangeOfSqlType(final DataType sqlType, final Object nonNull) {
        String m = String.format("%s[%s] literal don't support java type[%s]",
                sqlType.getClass().getName(), sqlType, nonNull.getClass().getName());
        return new CriteriaException(m);
    }

    public static CriteriaException outRangeOfSqlType(final DataType sqlType, final Object nonNull,
                                                      @Nullable Throwable cause) {
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

    public static CriteriaException valueOutRange(final DataType sqlType, final Object nonNull) {
        return valueOutRange(sqlType, nonNull, null);
    }

    public static CriteriaException valueOutRange(final DataType sqlType, final Object nonNull
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


    public static CriteriaException namedParamNotMatch(SqlValueParam.NamedMultiValue param, @Nullable Object value) {
        String m = String.format("named value[name:%s,size:%s] value[%s] isn't %s."
                , param.name(), param.columnSize(), ClassUtils.safeClassName(value), Collection.class.getName());
        return new CriteriaException(m);
    }

    public static CriteriaException namedMultiParamSizeError(SqlValueParam.NamedMultiValue param, int size) {
        String m = String.format("named collection parameters[name:%s,size:%s] value size[%s] error."
                , param.name(), param.columnSize(), size);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownParamValue(@Nullable SQLParam paramValue) {
        String m = String.format("unknown %s type %s", SQLParam.class.getName()
                , ClassUtils.safeClassName(paramValue));
        return new CriteriaException(m);
    }

    public static ArmyException unexpectedSqlParam(@Nullable SQLParam sqlParam) {
        String m;
        m = String.format("unexpected %s type %s", SQLParam.class.getName(), ClassUtils.safeClassName(sqlParam));
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


    public static CriteriaException independentDmlDontSupportNamedValue() {
        String m = "Only the batch update(delete) in multi-statement context support named parameter(literal).";
        return new CriteriaException(m);
    }


    public static NullPointerException optionValueIsNull(Option<?> option) {
        String m = String.format("error %s.nonNullOf(),the value of %s is null", OptionSpec.class.getName(), option);
        return new NullPointerException(m);
    }

    public static SessionFactoryException sessionFactoryClosed(SessionFactory factory) {
        String m = String.format("%s have closed.", factory);
        return new SessionFactoryException(m);
    }


    public static SessionException readOnlySession(Session session) {
        String m;
        m = String.format("%s of %s is read only,don't support DML.", session.sessionFactory(), session);
        return new ReadOnlySessionException(m);
    }

    public static SessionException writeSessionPseudoTransaction(Session session) {
        String m;
        m = String.format("%s isn't read only,don't support pseudo transaction.", session);
        return new SessionException(m);
    }

    public static IllegalArgumentException pseudoWriteError(Session session, TransactionOption option) {
        String m;
        m = String.format("%s isn't readonly,reject pseudo transaction %s.", session, option);
        return new IllegalArgumentException(m);
    }


    public static TransactionException readOnlyTransaction(Session session) {
        String m;
        m = String.format("%s of %s in read only transaction,don't support DML.", session.sessionFactory(), session);
        return new ReadOnlyTransactionException(m);
    }

    public static NoTransactionException noTransaction(Session session) {
        String m = String.format("%s no transaction", session);
        return new NoTransactionException(m);
    }

    public static TransactionException rollbackOnlyTransaction(Session session) {
        String m = String.format("current transaction rollback only of %s", session);
        return new TransactionUsageException(m);
    }

    public static RmSessionException xidIsNull() {
        return new RmSessionException("xid must be non-null", RmSessionException.XAER_INVAL);
    }


    public static RmSessionException xaGtridNoText() {
        return new RmSessionException("gtrid of xid must have text.", RmSessionException.XAER_NOTA);
    }

    public static RmSessionException xaBqualNonNullAndNoText() {
        return new RmSessionException("bqual of xid non-null and no text.", RmSessionException.XAER_NOTA);
    }

    public static RmSessionException xaGtridBeyond64Bytes() {
        return new RmSessionException("bytes length of gtrid beyond 64 bytes.", RmSessionException.XAER_NOTA);
    }

    public static RmSessionException xaBusyOnOtherTransaction() {
        return new RmSessionException("session is busy with another transaction", RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaDontSupportForget(Database database) {
        String m = String.format("%s don't support forget method.", database.name());
        return new RmSessionException(m, RmSessionException.XAER_RMERR);
    }

    public static RmSessionException xaInvalidFlag(final int flags, final String method) {
        String m = String.format("XA invalid flag[%s] for method %s", Integer.toBinaryString(flags), method);
        return new RmSessionException(m, RmSessionException.XAER_INVAL);
    }

    public static RmSessionException xaBqualBeyond64Bytes() {
        return new RmSessionException("bytes length of bqual beyond 64 bytes.", RmSessionException.XAER_NOTA);
    }

    public static RmSessionException xaTowPhaseXidConflict(Xid xid) {
        String m = String.format("xid[%s] tow phase commit conflict with current transaction.", xid);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaNonCurrentTransaction(@Nullable Xid xid) {
        String m = String.format("xid[%s] not current transaction.", xid);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaTransactionDontSupportEndCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("%s %s don't support end command", xid, states);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaStatesDontSupportPrepareCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("%s %s don't support prepare command", xid, states);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaStatesDontSupportCommitCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("%s %s don't support commit command", xid, states);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaStatesDontSupportRollbackCommand(@Nullable Xid xid, XaStates states) {
        String m = String.format("%s %s don't support rollback command", xid, states);
        return new RmSessionException(m, RmSessionException.XAER_PROTO);
    }

    public static RmSessionException xaTransactionRollbackOnly(@Nullable Xid xid) {
        String m = String.format("%s is rollback-only.", xid);
        return new RmSessionException(m, RmSessionException.XA_RBROLLBACK);
    }

    public static RmSessionException xaDontSupportForget(RmSession session) {
        String m = String.format("%s don't support forget command", session);
        return new RmSessionException(m, RmSessionException.XAER_RMERR);
    }

    public static TransactionException existsTransaction(Session session) {
        String m = String.format("%s exists transaction,couldn't start new transaction.", session);
        return new ExistsTransactionException(m);
    }


    public static SessionClosedException sessionClosed(Session session) {
        String m;
        m = String.format("%s have closed.", session);
        return new SessionClosedException(m);
    }


    public static SessionException childDmlNoTransaction(Session session, ChildTableMeta<?> table) {
        String m;
        m = String.format("%s no transaction,so you don't execute dml about child table %s."
                , session, table);
        return new ChildDmlNoTractionException(m);
    }


    public static QueryInsertException dontSupportSubQueryInsert(Session session) {
        String m;
        m = String.format("%s don't support query insert.", session);
        return new QueryInsertException(m);
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

    @Deprecated
    public static OptimisticLockException optimisticLock(long affectedRows) {
        String m = String.format("Affected rows is %s,don't satisfy expected rows.", affectedRows);
        return new OptimisticLockException(m);
    }

    public static OptimisticLockException optimisticLock() {
        return new OptimisticLockException("Affected rows is zero,don't satisfy expected rows.");
    }


    /**
     * @param batchNo based 1
     */
    public static OptimisticLockException batchOptimisticLock(@Nullable TableMeta<?> domainChild,
                                                              int batchNo, long affectedRows) {
        String m = String.format("%s Batch number[%s(based 1)] affected rows is %s,don't satisfy expected rows.",
                domainChild == null ? "" : domainChild, batchNo, affectedRows);
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


    public static NonMonoException nonSingleRow(List<?> list) {
        String m = String.format("select result[%s] more than 1.", list.size());
        return new NonMonoException(m);
    }

    public static NonMonoException nonMono() {
        return new NonMonoException("stream row count more than 1.");
    }

    public static NonMonoException nonUnique(Class<?> resultClass) {
        return new NonMonoException(String.format("return %s row count more than 1", resultClass.getName()));
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
                , TabularItem.class.getName(), ClassUtils.safeClassName(tableItem));
        return new CriteriaException(m);
    }

    public static CriteriaException selectionAliasNoText() {
        return new CriteriaException(String.format("%s alias must have text", Selection.class.getName()));
    }

    public static CriteriaException tabularAliasIsEmpty() {
        return new CriteriaException("tabular alias must non-empty.");
    }

    public static IllegalArgumentException streamApiDontSupportTwoStmtMode() {
        return new IllegalArgumentException("stream api don't support two statement mode.");
    }

    public static ObjectAccessException nonWritableProperty(Object target, String propertyName) {
        String m = String.format("%s property[%s] isn't writable.", target, propertyName);
        return new ObjectAccessException(m);
    }

    public static ObjectAccessException propertyTypeNotMatch(FieldMeta<?> field, Object value) {
        String m = String.format("%s java type is %s,but value type is %s,not match."
                , field, field.javaType().getName(), ClassUtils.safeClassName(value));
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

    public static MetaException timeFieldScaleError(FieldMeta<?> field) {
        String m = String.format("%s scale[%s] isn't default and not in [0,6]", field, field.scale());
        throw new MetaException(m);
    }


    public static CriteriaException identifierError(final String identifier, final Dialect dialect) {
        String m = String.format("identifier[%s] syntax error for %s", identifier, dialect);
        return new CriteriaException(m);
    }

    public static CriteriaException unknownArrayDimension(SQLType sqlType, MappingType type) {
        String m = String.format("unknown array type %s dimension of %s", sqlType, type);
        return new CriteriaException(m);
    }

    public static CriteriaException objectNameError(final DatabaseObject object, final Dialect dialect) {
        final String m, objectName;
        objectName = object.objectName();
        if (objectName.length() == 0) {
            m = String.format("%s %s objectName syntax error,it's empty. %s", DatabaseObject.class.getName(),
                    object, dialect);
        } else if (objectName.indexOf(_Constant.NUL_CHAR) > -1) {
            m = String.format("%s %s objectName syntax error,contains NUL char. %s", DatabaseObject.class.getName(),
                    object, dialect);
        } else {
            m = String.format("%s %s objectName syntax error. %s", DatabaseObject.class.getName(), object, dialect);
        }
        return new CriteriaException(m);
    }

    public static MetaException serverMetaError(ServerMeta meta) {
        String m = String.format("%s error", meta);
        return new MetaException(m);
    }

    public static MetaException mapMethodError(MappingType type, Class<? extends SQLType> sqlType) {
        String m = String.format("%s map(%s) don't return %s", type, ServerMeta.class.getName(), sqlType.getName());
        return new MetaException(m);
    }

    public static MetaException notUserDefinedType(MappingType type, DataType sqlType) {
        String m = String.format("%s return %s but don't implements %s .", type.getClass().getName(), sqlType,
                MappingType.SqlUserDefinedType.class.getName());
        return new MetaException(m);
    }

    public static MetaException unrecognizedType(final Database database, DataType dataType) {
        String m = String.format("%s is unrecognized type for %s and %s is false.", dataType, database.name(),
                ArmyKey.UNRECOGNIZED_TYPE_ALLOWED.name);
        return new MetaException(m);
    }

    public static MetaException unrecognizedTypeLiteral(final Database database, DataType dataType) {
        String m = String.format("%s is unrecognized type for %s , so couldn't output literal.", dataType,
                database.name());
        return new MetaException(m);
    }

    public static DataAccessException unknownIsolation(Isolation isolation) {
        return new DataAccessException(String.format("unknown isolation %s", isolation));
    }

    public static IllegalArgumentException notArrayMappingType(MappingType type) {
        String m = String.format("%s isn't %s type.", type, MappingType.SqlArrayType.class.getName());
        return new IllegalArgumentException(m);
    }


    public static CriteriaException operatorError(Object operator, Database database) {
        String m = String.format("%s is not supported by %s.", operator, database);
        return new CriteriaException(m);
    }


    public static CriteriaException dontSupportEscapeMode(EscapeMode mode, Dialect dialect) {
        String m = String.format("%s is not supported by %s.", mode, dialect);
        return new CriteriaException(m);
    }

    public static IllegalOneStmtModeException parentSubInsertDomainError(TableMeta<?> actual, ChildTableMeta<?> child) {
        String m = String.format("excepted domain is %s but %s", actual, child);
        return new IllegalOneStmtModeException(m);
    }

    public static CriteriaException parentSubInsertDomainUnknown(ParentTableMeta<?> parent) {
        String m = String.format("%s domain is unknown", parent);
        return new CriteriaException(m);
    }


    public static CriteriaException notFoundMappingType(Object value) {
        String m = String.format("Not found default %s for %s.", MappingType.class.getName(),
                ClassUtils.safeClassName(value));
        return new CriteriaException(m);
    }

    public static CriteriaException funcNotFoundMappingType(String funcName, Object value) {
        String m = String.format("function[%s] Not found default %s for %s.", funcName, MappingType.class.getName(),
                ClassUtils.safeClassName(value));
        return new CriteriaException(m);
    }

    public static CriteriaException unknownRowElement(Object element) {
        String m = String.format("unknown row element %s", element);
        return new CriteriaException(m);
    }


    public static ChildUpdateException parentChildRowsNotMatch(Session session, ChildTableMeta<?> domainTable,
                                                               long parentRows, long childRows) {
        String m = String.format("session[%s]\n %s parent insert/update rows[%s] and child insert/update rows[%s] not match.",
                session.name(), domainTable, parentRows, childRows);
        return new ChildUpdateException(m);
    }

    public static ChildUpdateException parentChildRowsNotMatch(String sessionName, long parentRows, long childRows) {
        String m = String.format("session[%s]\n  parent insert/update rows[%s] and child insert/update rows[%s] not match.",
                sessionName, parentRows, childRows);
        return new ChildUpdateException(m);
    }

    public static ChildUpdateException childInsertError(Session session, ChildTableMeta<?> domainTable, Throwable clause) {
        String m = String.format("%s\n parent of %s  insert completion,but child insert occur error.",
                session.name(), domainTable);
        return new ChildUpdateException(m, clause);
    }


    public static ChildUpdateException childUpdateError(Session session, ChildTableMeta<?> domainTable, Throwable clause) {
        String m = String.format("%s\n child of %s  update completion,but parent update occur error.",
                session, domainTable);
        return new ChildUpdateException(m, clause);
    }


    public static NullPointerException listConstructorError() {
        return new NullPointerException("listConstructor return null");
    }

    public static DataAccessException multiStmtCountAndResultCountNotMatch(@Nullable TableMeta<?> table, int stmtCount,
                                                                           int resultCount) {
        String m = String.format("%s multi-statement count[%s] and result count[%s] not match.",
                table == null ? "" : table,
                stmtCount, resultCount);
        return new DataAccessException(m);
    }

    /**
     * @param batchNo based 1
     */
    public static DataAccessException batchUpdateReturnResultSet(@Nullable TableMeta<?> table, int batchNo) {

        return new DataAccessException(String.format("%s batch update[number:%s(based 1)] return result set",
                table == null ? "" : table,
                batchNo));
    }

    public static DataAccessException unsupportedDatabaseFamily(String productFamily) {
        return new DataAccessException(String.format("currently,unsupported database product family %s .", productFamily));
    }

    /**
     * @param batchNum based 1
     */
    public static ChildUpdateException batchChildUpdateRowsError(@Nullable ChildTableMeta<?> domainTable, int batchNum,
                                                                 long childRows, long parentRows) {
        String m = String.format("%s child [batch %s(based 1) : %s rows] and parent[batch %s(based 1) : %s rows] not match.",
                domainTable == null ? "" : domainTable, batchNum, childRows, batchNum, parentRows);
        return new ChildUpdateException(m);
    }

    public static ChildUpdateException childBatchSizeError(ChildTableMeta<?> table, int childBatchSize,
                                                           int parentBatchSize) {
        String m = String.format("%s child batch number[%s] and parent batch number[%s] not match.",
                table, childBatchSize, parentBatchSize);
        return new ChildUpdateException(m);
    }

    public static DataAccessException multiStmtBatchUpdateResultCountError(int statementCount, int actual) {
        String m;
        m = String.format("error, multi-statement batch update result count[%s] and statement count[%s]",
                actual, statementCount);
        throw new DataAccessException(m);
    }

    public static DataAccessException columnGetError(int indexBasedZero, String columnLabel, Throwable cause) {
        String m = String.format("column[index(%s) , label (%s)] get error.", indexBasedZero, columnLabel);
        return new DataAccessException(m, cause);
    }

    public static NullPointerException terminatorIsNull() {
        return new NullPointerException("terminator is null");
    }

    public static NullPointerException objectConstructorError() {
        return new NullPointerException("object constructor return null");
    }

    public static IllegalArgumentException unknownSelectionAlias(String selectionAlias) {
        return new IllegalArgumentException(String.format("unknown selection alias[%s]", selectionAlias));
    }

    public static IllegalArgumentException unknownSavePoint(Object savePoint) {
        return new IllegalArgumentException(String.format("unknown save point instance %s", savePoint));
    }

    public static IllegalArgumentException recordFuncError(Function<? super CurrentRecord, ?> function, CurrentRecord record) {
        String m = String.format("record function %s couldn't return %s", function, record);
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException recordFuncReturnNull(Function<? super CurrentRecord, ?> function) {
        String m = String.format("record function %s couldn't return null", function);
        return new IllegalArgumentException(m);
    }


    public static DataAccessException noMoreResult() {
        return new DataAccessException("No more result.");
    }

    public static DataAccessException notExistsAnyResultSet() {
        return new DataAccessException("database don't return any result set.");
    }

    public static DataAccessException currentResultIsQuery() {
        return new DataAccessException("current result is query result,isn't update result.");
    }

    public static DataAccessException unknownColumnLabel(String columnLabel) {
        return new DataAccessException(String.format("unknown column label[%s]", columnLabel));
    }

    public static DataAccessException currentResultIsUpdate() {
        return new DataAccessException("current result is update result,isn't query result.");
    }

    public static DataAccessException unknownStmtItem(MultiStmt.StmtItem item) {
        return new DataAccessException(String.format("unknown %s %s .", MultiStmt.StmtItem.class.getName(), item));
    }

    public static DataAccessException stmtItemIsUpdateItem(MultiStmt.UpdateStmt item) {
        String m = String.format("%s is %s ,but database return query result.", MultiStmt.StmtItem.class.getName(),
                item);
        return new DataAccessException(m);
    }

    public static DataAccessException insertedRowsAndGenerateIdNotMatch(int insertedRows, long actualCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows,
                actualCount);
        return new DataAccessException(m);
    }

    public static ArmyException recordMapFuncReturnError(Function<? super CurrentRecord, ?> function) {
        final String className = CurrentRecord.class.getName();
        String m = String.format("%s map function %s return %s", className, function, className);
        return new ArmyException(m);
    }

    public static DataAccessException batchQueryHaveEnded() {
        return new DataAccessException("batch query have ended");
    }

    public static DataAccessException lastStreamDontEnd() {
        return new DataAccessException("last stream not end,you don't create new stream");
    }

    public static DataAccessException multiResultHaveClosed() {
        return new DataAccessException("multi-result have closed");
    }

    public static NullPointerException recordMapFuncIsNull() {
        return new NullPointerException("row map Function is null");
    }

    public static NullPointerException statesConsumerIsNull() {
        return new NullPointerException("states Consumer is null");
    }

    public static ArmyException recordMapFuncInvokeError(Function<? super CurrentRecord, ?> function) {
        String m = String.format("%s map function %s throw error", CurrentRecord.class.getName(), function);
        return new ArmyException(m);
    }

    public static ArmyException statesConsumerInvokeError(Consumer<ResultStates> consumer, Throwable cause) {
        String m = String.format("%s Consumer %s throw error", ResultStates.class.getName(), consumer);
        return new ArmyException(m, cause);
    }


    public static DataAccessException exceptedError(int indexBasedOne, MultiStmt.StmtItem excepted,
                                                    String actual) {
        String m = String.format("No %s item excepted is %s but actual %s", indexBasedOne, excepted, actual);
        return new DataAccessException(m);
    }

    public static DataAccessException columnCountAndSelectionCountNotMatch(int columnSize, int selectionCount) {
        String m = String.format("result column count[%s] and selection count[%s] not match.",
                columnSize, selectionCount);
        return new DataAccessException(m);
    }

    public static DataAccessException batchQueryReturnUpdate() {
        return new DataAccessException("batch query return update result.");
    }


    public static IllegalStateException convertFail(ArmyKey<?> key, Object userValue, @Nullable Throwable cause) {
        String m = String.format("couldn't convert key[%s] %s type to %s", key.name,
                userValue.getClass().getName(), key.javaType.getName());
        final IllegalStateException e;
        if (cause == null) {
            e = new IllegalStateException(m);
        } else {
            e = new IllegalStateException(m, cause);
        }
        return e;
    }

    public static DataAccessException firstStmtIdIsNull() {
        return new DataAccessException("first statement id is null");
    }

    public static DataAccessException idValueIsNull(int rowIndexBasedZero, PrimaryFieldMeta<?> field) {
        String m = String.format("Number %s row id value is null for %s", rowIndexBasedZero + 1, field);
        return new DataAccessException(m);
    }

    public static DataAccessException secondStmtIdIsNull(Selection idSelection) {
        String m;
        if (idSelection instanceof FieldSelection) {
            m = String.format("second statement id %s is null", ((FieldSelection) idSelection).fieldMeta());
        } else {
            // criteria package no bug,never here
            m = "second statement id is null";
        }
        return new DataAccessException(m);
    }

    public static DataAccessException noMatchFirstStmtRow(Object secondStmtRowId) {
        String m = String.format("No match row of first statement for second statement row id[%s]", secondStmtRowId);
        return new DataAccessException(m);
    }

    public static DataAccessException duplicateIdValue(int rowIndexBasedZero, PrimaryFieldMeta<?> field, Object value) {
        String m = String.format("database return duplicate value(new value: %s) for number %s row %s",
                value, rowIndexBasedZero + 1, field);
        return new DataAccessException(m);
    }


    public static IllegalOneStmtModeException oneStmtModePostChildNoIdExpression(final Database database, final ChildTableMeta<?> child) {
        String m = String.format("error,you use %s one statement mode values syntax insert %s,but no child id default scalar expression",
                database, child);
        return new IllegalOneStmtModeException(m);
    }

    public static IllegalArgumentException recordColumnCountNotMatch(SqlRecord record, int columnSize, MappingType type) {
        String m = String.format("%s column count[%s] and column count[%s] of %s not match",
                record.getClass().getName(), record.size(), columnSize, type.getClass().getName());
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException parenNotMatch(String fragment) {
        String m = String.format("fragment[%s] left paren and right paren not match", fragment);
        return new IllegalArgumentException(m);
    }

    public static IllegalArgumentException doubleQuoteNotMatch() {
        return new IllegalArgumentException("double quote count not match");
    }

    public static DataAccessException insertedRowsAndGenerateIdNotMatch(int insertedRows, int actualCount) {
        String m = String.format("insertedRows[%s] and generateKeys count[%s] not match.", insertedRows,
                actualCount);
        return new DataAccessException(m);
    }


    private static final class ExistsTransactionException extends TransactionException {

        private ExistsTransactionException(String message) {
            super(message);
        }

    }//ExistsTransactionException


}
