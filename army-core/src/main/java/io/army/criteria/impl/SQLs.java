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

package io.army.criteria.impl;

import io.army.annotation.UpdateMode;
import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.inner.*;
import io.army.criteria.standard.StandardDelete;
import io.army.criteria.standard.StandardInsert;
import io.army.criteria.standard.StandardQuery;
import io.army.criteria.standard.StandardUpdate;
import io.army.dialect._Constant;
import io.army.dialect._SetClauseContext;
import io.army.dialect._SqlContext;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.PostgreSQL;

/**
 * <p>
 * This class is util class used to create standard sql statement.
 */
public abstract class SQLs extends SQLSyntax {

    /**
     * private constructor
     */
    private SQLs() {
    }


    public static final WordAll ALL = SqlWords.KeyWordAll.ALL;

    public static final WordDistinct DISTINCT = SqlWords.KeyWordDistinct.DISTINCT;

    public static final WordLateral LATERAL = SqlWords.KeyWordLateral.LATERAL;

    public static final WordFirst FIRST = SqlWords.KeyWordFirst.FIRST;

    public static final WordNext NEXT = SqlWords.KeyWordNext.NEXT;

    public static final WordPercent PERCENT = SqlWords.KeyWordPercent.PERCENT;

    public static final NullsFirstLast NULLS_FIRST = SqlWords.KeyWordsNullsFirstLast.NULLS_FIRST;

    public static final NullsFirstLast NULLS_LAST = SqlWords.KeyWordsNullsFirstLast.NULLS_LAST;

    public static final WordOnly ONLY = SqlWords.KeyWordOny.ONLY;

    public static final WordRow ROW = SqlWords.KeyWordRow.ROW;

    public static final WordRows ROWS = SqlWords.KeyWordRows.ROWS;

    public static final WordLines LINES = SqlWords.KeyWordLines.LINES;

    public static final WordInterval INTERVAL = SqlWords.KeyWordInterval.INTERVAL;

    public static final WordsWithTies WITH_TIES = SqlWords.KeyWordWithTies.WITH_TIES;

    public static final BooleanTestWord UNKNOWN = SqlWords.KeyWordUnknown.UNKNOWN;


    public static final WordMaterialized MATERIALIZED = SqlWords.KeyWordMaterialized.MATERIALIZED;

    public static final WordMaterialized NOT_MATERIALIZED = SqlWords.KeyWordMaterialized.NOT_MATERIALIZED;

    /**
     * package field
     */
    static final AscDesc ASC = SqlWords.KeyWordAscDesc.ASC;
    /**
     * package field
     */
    static final AscDesc DESC = SqlWords.KeyWordAscDesc.DESC;


    public static final WordAs AS = SqlWords.KeyWordAs.AS;

    public static final WordTo TO = SqlWords.KeyWordTo.TO;

    public static final WordAnd AND = SqlWords.KeyWordAnd.AND;


    public static final TrimSpec BOTH = SqlWords.WordTrimPosition.BOTH;
    public static final TrimSpec LEADING = SqlWords.WordTrimPosition.LEADING;
    public static final TrimSpec TRAILING = SqlWords.WordTrimPosition.TRAILING;

    public static final WordIn IN = SqlWords.KeyWordIn.IN;

    public static final WordFor FOR = SqlWords.KeyWordFor.FOR;

    public static final WordFrom FROM = SqlWords.KeyWordFrom.FROM;

    public static final WordSimilar SIMILAR = SqlWords.KeyWordSimilar.SIMILAR;

    public static final WordOn ON = SqlWords.KeyWordOn.ON;

    public static final Window.RowModifier UNBOUNDED_PRECEDING = SQLWindow.WindowRowModifier.UNBOUNDED_PRECEDING;

    public static final Window.RowModifier CURRENT_ROW = SQLWindow.WindowRowModifier.CURRENT_ROW;

    public static final Window.RowModifier UNBOUNDED_FOLLOWING = SQLWindow.WindowRowModifier.UNBOUNDED_FOLLOWING;

    public static final Window.ExpModifier PRECEDING = SQLWindow.WindowExpModifier.PRECEDING;

    public static final Window.ExpModifier FOLLOWING = SQLWindow.WindowExpModifier.FOLLOWING;


    @Support({PostgreSQL, H2})
    public static final BetweenModifier SYMMETRIC = SqlWords.KeyWordSymmetric.SYMMETRIC;

    @Support({H2})
    public static final BetweenModifier ASYMMETRIC = SqlWords.KeyWordSymmetric.ASYMMETRIC;

    public static final SymbolAsterisk ASTERISK = SqlWords.SQLSymbolAsterisk.ASTERISK;

    public static final SymbolPeriod PERIOD = SqlWords.SQLSymbolPeriod.PERIOD;

    public static final SymbolSpace SPACE = SqlWords.SymbolSpaceEnum.SPACE;

    public static final SymbolEqual EQUAL = SqlWords.SymbolEqualEnum.EQUAL;

    public static final SymbolColonEqual COLON_EQUAL = SqlWords.SymbolColonEqualEnum.COLON_EQUAL;

    public static final WordBooleans TRUE = OperationPredicate.booleanWord(true);

    public static final WordBooleans FALSE = OperationPredicate.booleanWord(false);


    public static final WordDefault DEFAULT = new DefaultWord();

    public static final WordNull NULL = NonOperationExpression.nullWord();


    public static final QuantifiedWord SOME = SqlWords.QueryOperator.SOME;

    public static final QuantifiedWord ANY = SqlWords.QueryOperator.ANY;

    public static final IsComparisonWord DISTINCT_FROM = SqlWords.IsComparisonKeyWord.DISTINCT_FROM;

    public static final WordJoin JOIN = SqlWords.KeyWordJoin.JOIN;

    public static final WordsOrderBy ORDER_BY = SqlWords.KeyWordsOrderBy.ORDER_BY;

    public static final WordsGroupBy GROUP_BY = SqlWords.KeyWordsGroupBy.GROUP_BY;

    public static final WordEscape ESCAPE = SqlWords.KeyWordEscape.ESCAPE;


    public static final WordPath PATH = SqlWords.KeyWordPath.PATH;

    public static final WordExists EXISTS = SqlWords.KeyWordExists.EXISTS;

    public static final WordColumns COLUMNS = SqlWords.KeyWordColumns.COLUMNS;

    public static final WordNested NESTED = SqlWords.KeyWordNested.NESTED;

    public static final WordsForOrdinality FOR_ORDINALITY = SqlWords.KeyWordsForOrdinality.FOR_ORDINALITY;

    public static final WordError ERROR = SqlWords.KeyWordError.ERROR;

    public static final NullOption NOT_NULL = SqlWords.KeyWordNotNull.NOT_NULL;

    public static final WordsCharacterSet CHARACTER_SET = SqlWords.KeyWordsCharacterSet.CHARACTER_SET;

    public static final WordCollate COLLATE = SqlWords.KeyWordsCollate.COLLATE;

    public static final WordUsing USING = SqlWords.KeyWordUsing.USING;

    public static final SQLWords COMMA = SqlWords.FuncWord.COMMA;

    public static final WordsAtTimeZone AT_TIME_ZONE = SqlWords.KeyWordsAtTimeZone.AT_TIME_ZONE;

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">MySQL SET Syntax for Variable Assignment</a>
     */
    public static final VarScope AT = SqlWords.KeyWordVarScope.AT;

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">MySQL SET Syntax for Variable Assignment</a>
     */
    public static final VarScope GLOBAL = SqlWords.KeyWordVarScope.GLOBAL;

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">MySQL SET Syntax for Variable Assignment</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-set.html">Postgre SET Syntax for Variable Assignment</a>
     */
    public static final VarScope SESSION = SqlWords.KeyWordVarScope.SESSION;

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">MySQL SET Syntax for Variable Assignment</a>
     * @see <a href="https://www.postgresql.org/docs/current/sql-set.html">Postgre SET Syntax for Variable Assignment</a>
     */
    public static final VarScope LOCAL = SqlWords.KeyWordVarScope.LOCAL;


    /*-------------------below literal -------------------*/


    public static final LiteralExpression LITERAL_0 = SQLs.literal(IntegerType.INSTANCE, 0);

    public static final LiteralExpression LITERAL_1 = SQLs.literal(IntegerType.INSTANCE, 1);

    public static final LiteralExpression LITERAL_2 = SQLs.literal(IntegerType.INSTANCE, 2);

    public static final LiteralExpression LITERAL_10 = SQLs.literal(IntegerType.INSTANCE, 10);

    public static final LiteralExpression LITERAL_DECIMAL_0;

    /**
     * @see #PARAM_EMPTY_STRING
     */
    public static final LiteralExpression LITERAL_EMPTY_STRING = SQLs.literal(StringType.INSTANCE, "");

    /**
     * @see #BATCH_NO_PARAM
     */
    public static final LiteralExpression BATCH_NO_LITERAL = SQLs.namedLiteral(IntegerType.INSTANCE, "$ARMY_BATCH_NO$");

    public static final Expression UPDATE_TIME_LITERAL_PLACEHOLDER = NonOperationExpression.updateTimeLiteralPlaceHolder();

    /*-------------------below param -------------------*/

    public static final ParamExpression PARAM_0 = SQLs.param(IntegerType.INSTANCE, 0);

    public static final ParamExpression PARAM_1 = SQLs.param(IntegerType.INSTANCE, 1);

    public static final ParamExpression PARAM_2 = SQLs.param(IntegerType.INSTANCE, 2);

    public static final ParamExpression PARAM_10 = SQLs.param(IntegerType.INSTANCE, 10);

    public static final ParamExpression PARAM_DECIMAL_0;

    public static final Expression UPDATE_TIME_PARAM_PLACEHOLDER = NonOperationExpression.updateTimeParamPlaceHolder();

    /**
     * @see #TRUE
     */
    public static final ParamExpression PARAM_TRUE = SQLs.param(BooleanType.INSTANCE, Boolean.TRUE);

    /**
     * @see #FALSE
     */
    public static final ParamExpression PARAM_FALSE = SQLs.param(BooleanType.INSTANCE, Boolean.FALSE);

    /**
     * @see #LITERAL_EMPTY_STRING
     */
    public static final ParamExpression PARAM_EMPTY_STRING = SQLs.param(StringType.INSTANCE, "");

    /**
     * @see #BATCH_NO_LITERAL
     */
    public static final ParamExpression BATCH_NO_PARAM = SQLs.namedParam(IntegerType.INSTANCE, "$ARMY_BATCH_NO$");


    static {
        final BigDecimal zero = new BigDecimal("0.00");
        LITERAL_DECIMAL_0 = SQLs.literal(BigDecimalType.INSTANCE, zero);
        PARAM_DECIMAL_0 = SQLs.param(BigDecimalType.INSTANCE, zero);
    }


    /**
     * package field
     */
    static final Expression _ASTERISK_EXP = new LiteralSymbolAsterisk();

    static final UnaryOperator<Select> SELECT_IDENTITY = SQLs::identity;

    static final UnaryOperator<SubQuery> SUB_QUERY_IDENTITY = SQLs::identity;

    static final Function<SubQuery, Expression> SCALAR_SUB_QUERY = Expressions::scalarExpression;

    static final UnaryOperator<Insert> INSERT_IDENTITY = SQLs::identity;

    static final UnaryOperator<Insert> UPDATE_IDENTITY = SQLs::identity;


    public static StandardInsert._PrimaryOption20Spec<Insert> singleInsert() {
        return StandardInserts.singleInsert20();
    }

    public static StandardInsert._PrimaryOption20Spec<Insert> singleInsert20() {
        return StandardInserts.singleInsert20();
    }


    public static StandardUpdate._DomainUpdateClause<Update> domainUpdate() {
        return StandardUpdates.simpleDomain();
    }


    public static StandardUpdate._WithSpec<Update> singleUpdate() {
        return StandardUpdates.singleUpdate(StandardDialect.STANDARD20);
    }

    public static StandardUpdate._WithSpec<Update> singleUpdate20() {
        return StandardUpdates.singleUpdate(StandardDialect.STANDARD20);
    }


    /**
     * <p>
     * Batch domain update
     */
    public static StandardUpdate._DomainUpdateClause<Statement._BatchUpdateParamSpec> batchDomainUpdate() {
        return StandardUpdates.batchDomain();
    }


    /**
     * <p>
     * Batch domain update
     */
    public static StandardUpdate._WithSpec<Statement._BatchUpdateParamSpec> batchSingleUpdate() {
        return StandardUpdates.batchSingleUpdate(StandardDialect.STANDARD20);
    }

    public static StandardUpdate._WithSpec<Statement._BatchUpdateParamSpec> batchSingleUpdate20() {
        return StandardUpdates.batchSingleUpdate(StandardDialect.STANDARD20);
    }


    public static StandardDelete._WithSpec<Delete> singleDelete() {
        return StandardDeletes.singleDelete(StandardDialect.STANDARD20);
    }

    public static StandardDelete._WithSpec<Delete> singleDelete20() {
        return StandardDeletes.singleDelete(StandardDialect.STANDARD20);
    }


    public static StandardDelete._DomainDeleteClause<Delete> domainDelete() {
        return StandardDeletes.domainDelete();
    }

    /**
     * <p>
     * Batch domain delete
     */
    public static StandardDelete._WithSpec<Statement._BatchDeleteParamSpec> batchSingleDelete() {
        return StandardDeletes.batchSingleDelete(StandardDialect.STANDARD20);
    }

    /**
     * <p>
     * Batch domain delete
     */
    public static StandardDelete._WithSpec<Statement._BatchDeleteParamSpec> batchSingleDelete20() {
        return StandardDeletes.batchSingleDelete(StandardDialect.STANDARD20);
    }


    public static StandardDelete._DomainDeleteClause<Statement._BatchDeleteParamSpec> batchDomainDelete() {
        return StandardDeletes.batchDomainDelete();
    }


    public static StandardQuery.WithSpec<Select> query() {
        return StandardQueries.simpleQuery(StandardDialect.STANDARD20, SELECT_IDENTITY);
    }

    public static StandardQuery.SelectSpec<Statement._BatchSelectParamSpec> batchQuery() {
        return StandardQueries.batchQuery(StandardDialect.STANDARD10);
    }

    public static StandardQuery.WithSpec<Select> query20() {
        return StandardQueries.simpleQuery(StandardDialect.STANDARD20, SELECT_IDENTITY);
    }

    public static StandardQuery.WithSpec<Statement._BatchSelectParamSpec> batchQuery20() {
        return StandardQueries.batchQuery(StandardDialect.STANDARD20);
    }

    public static StandardQuery.SelectSpec<SubQuery> subQuery() {
        return StandardQueries.subQuery(StandardDialect.STANDARD10, ContextStack.peek(), SUB_QUERY_IDENTITY);
    }

    public static StandardQuery.WithSpec<SubQuery> subQuery20() {
        return StandardQueries.subQuery(StandardDialect.STANDARD20, ContextStack.peek(), SUB_QUERY_IDENTITY);
    }


    public static StandardQuery.SelectSpec<Expression> scalarSubQuery() {
        return StandardQueries.subQuery(StandardDialect.STANDARD10, ContextStack.peek(), SCALAR_SUB_QUERY);
    }

    public static StandardQuery.WithSpec<Expression> scalarSubQuery20() {
        return StandardQueries.subQuery(StandardDialect.STANDARD20, ContextStack.peek(), SCALAR_SUB_QUERY);
    }



    /*-------------------below package method-------------------*/

    static AssignmentItem _assignmentItem(final SqlField field, final @Nullable Object value) {
        final AssignmentItem item;
        if (value instanceof AssignmentItem) {
            item = (AssignmentItem) value;
        } else {
            item = SQLs.param(field, value);
        }
        return item;
    }

    static Expression _nonNullExp(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndCriteriaError("appropriate operator don't allow that operand is null");
        }
        if (value instanceof Expression) {
            return (Expression) value;
        }

        if (value instanceof RightOperand) {
            String m = String.format("appropriate operator don't allow that operand is %s",
                    value.getClass().getName());
            throw new CriteriaException(m);
        }
        return SQLs.paramValue(value);
    }

    static Expression _nullableExp(final @Nullable Object value) {
        final Expression exp;
        if (value == null) {
            exp = SQLs.NULL;
        } else if (value instanceof Expression) {
            exp = (Expression) value;
        } else if (value instanceof RightOperand) {
            String m = String.format("appropriate operator don't allow that operand is %s",
                    value.getClass().getName());
            throw new CriteriaException(m);
        } else {
            exp = SQLs.paramValue(value);
        }
        return exp;
    }

    static Expression _nonNullLiteral(final @Nullable Object value) {
        if (value == null) {
            throw ContextStack.clearStackAndCriteriaError("appropriate operator don't allow that operand is null");
        }
        if (value instanceof Expression) {
            return (Expression) value;
        }
        return SQLs.literalValue(value);
    }

    static Expression _nullableLiteral(final @Nullable Object value) {
        final Expression expression;
        if (value == null) {
            expression = SQLs.NULL;
        } else if (value instanceof Expression) {
            expression = (Expression) value;
        } else {
            expression = SQLs.literalValue(value);
        }
        return expression;
    }


    /**
     * <p>
     * package method that is used by army developer.
     * *
     *
     * @param value {@link Expression} or parameter.
     * @see #plusEqual(SqlField, Expression)
     */
    static SQLs.ArmyItemPair _itemPair(final @Nullable SqlField field, final @Nullable AssignOperator operator,
                                       final @Nullable Expression value) {
        if (field == null || value == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        //TODO right operand non-null validate
        final SQLs.ArmyItemPair itemPair;
        if (operator == null) {
            itemPair = new SQLs.FieldItemPair(field, (ArmyExpression) value);
        } else {
            itemPair = new SQLs.OperatorItemPair(field, operator, (ArmyExpression) value);
        }
        return itemPair;
    }

    /**
     * <p>
     * package method that is used by army developer.
     */
    static _ItemPair _itemExpPair(final SqlField field, @Nullable Expression value) {
        assert value != null;
        return SQLs._itemPair(field, null, value);
    }

    static ItemPair _itemPair(List<? extends SqlField> fieldList, SubQuery subQuery) {
        return new SQLs.RowItemPair(fieldList, subQuery);
    }


    /**
     * <p>
     * This method is similar to {@link Function#identity()}, except that use method reference.
     * *
     *
     * @see Function#identity()
     */
    static <T extends Item> T identity(T t) {
        return t;
    }


    static Item castCriteria(Item stmt) {
        throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
    }


    @Deprecated
    static BatchUpdate _batchUpdateIdentity(UpdateStatement update) {
        return (BatchUpdate) update;
    }

    static BatchDelete _batchDeleteIdentity(DeleteStatement delete) {
        return (BatchDelete) delete;
    }


    static <I extends Item> Function<TypeInfer, I> _toSelection(final Function<Selection, I> function) {
        return t -> {
            if (!(t instanceof Selection)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return function.apply((Selection) t);
        };
    }

    static <I extends Item> Function<TypeInfer, I> _ToExp(final Function<Expression, I> function) {
        return t -> {
            if (!(t instanceof Expression)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return function.apply((Expression) t);
        };
    }





    static String keyWordsToString(Enum<?> wordEnum) {
        return _StringUtils.builder(20)
                .append(SQLs.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(wordEnum.name())
                .toString();
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordAll extends Modifier, QuantifiedWord {

    }

    public interface WordDistinct extends Modifier, ArgDistinct {

    }

    public interface WordOn {

    }


    public interface SymbolSpace {

    }

    public interface SymbolEqual {

    }

    public interface SymbolPeriod {

    }

    /**
     * @see SQLs#DISTINCT
     */
    public interface ArgDistinct extends SQLWords {

    }

    public interface SymbolAsterisk {

    }

    public interface SymbolColonEqual {

    }

    public interface WordNull extends BooleanTestWord, Expression, NullOption { // extends Expression not SimpleExpression

    }

    public interface WordAs extends SQLWords {

    }

    public interface WordTo extends SQLWords {

    }

    public interface WordDefault extends Expression {

    }

    public interface WordBooleans extends BooleanTestWord, SimplePredicate {

    }

    public interface QuantifiedWord extends SQLWords {

    }

    public interface WordAnd {

    }

    public interface WordEscape extends SQLWords {

    }

    public interface NullOption {

    }

    public interface BooleanTestWord extends SQLWords {

    }

    public interface DocumentValueOption extends SQLWords {

    }

    public interface WordDocument extends BooleanTestWord, DocumentValueOption {

    }

    public interface WordContent extends DocumentValueOption {

    }


    public interface _ArrayConstructorSpec extends ArrayExpression {

        ArrayExpression castTo(MappingType type);

    }

    public interface IsComparisonWord extends SQLWords {

    }

    public interface BetweenModifier extends SQLWords {

    }

    public interface WordInterval extends SQLWords {

    }

    public interface WordPercent {

    }

    public interface WordOnly extends TableModifier, FetchOnlyWithTies, SQLWords {

    }

    public interface WordFirst extends FetchFirstNext {

    }

    public interface WordNext extends FetchFirstNext {

    }

    public interface WordRow extends FetchRow {

    }

    /**
     * <p>This interface is base interface of following :
     * <ul>
     *     <li>{@link WordRows}</li>
     *     <li>{@link WordLines}</li>
     * </ul>
     */
    public interface LinesWord {

    }

    public interface WordRows extends FetchRow, LinesWord {

    }

    public interface WordLines extends SQLWords, LinesWord {

    }

    public interface WordLateral extends DerivedModifier {

    }

    public interface WordsWithTies extends FetchOnlyWithTies {

    }

    public interface WordPath extends SQLWords {

    }


    public interface WordsForOrdinality extends SQLWords {

    }

    public interface WordIn {

    }

    public interface WordFrom {

    }

    public interface WordFor {

    }

    public interface WordSimilar {

    }

    public interface WordColumns extends SQLWords {

    }

    public interface WordNested extends SQLWords {

    }

    public interface WordExists extends SQLWords {

    }

    public interface WordError extends SQLWords {

    }

    public interface TrimSpec {

    }

    public interface WordsCharacterSet extends SQLWords {

    }

    public interface WordCollate extends SQLWords {

    }

    public interface WordUsing extends SQLWords {

    }

    public interface TableModifier extends SQLWords {

    }

    public interface DerivedModifier extends SQLWords {

    }

    public interface FetchFirstNext {

    }

    public interface FetchRow {

    }

    public interface FetchOnly {

    }

    public interface FetchWithTies {

    }

    public interface FetchOnlyWithTies extends FetchOnly, FetchWithTies {

    }

    public interface AscDesc extends SQLWords {

    }

    public interface NullsFirstLast extends SQLWords {

    }


    public interface IndexHintPurpose extends SQLWords {

    }

    public interface WordJoin extends IndexHintPurpose {

    }

    public interface WordsOrderBy extends IndexHintPurpose {

    }

    public interface WordsGroupBy extends IndexHintPurpose {

    }

    public interface WordsAtTimeZone extends SQLWords {

    }

    public interface VarScope {

        String name();
    }

    public interface WordMaterialized extends SQLWords {

    }






    /*-------------------below package method-------------------*/


    static abstract class ArmyItemPair implements _ItemPair {

        final RightOperand right;

        private ArmyItemPair(RightOperand right) {
            this.right = right;
        }
    }//ArmyItemPair

    /**
     * @see #_itemPair(SqlField, AssignOperator, Expression)
     */
    static class FieldItemPair extends ArmyItemPair implements _ItemPair._FieldItemPair {

        final SqlField field;

        private FieldItemPair(SqlField field, ArmyExpression value) {
            super(value);
            this.field = field;
        }

        @Override
        public final void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final SqlField field = this.field;
            final _Expression right = (_Expression) this.right;

            if (right == SQLs.UPDATE_TIME_PARAM_PLACEHOLDER) {
                if (this instanceof OperatorItemPair) {
                    throw placeholderError("UPDATE_TIME_PARAM_PLACEHOLDER");
                }
                context.appendSetLeftItem(field, right); //  append left item
            } else if (right == SQLs.UPDATE_TIME_LITERAL_PLACEHOLDER) {
                if (this instanceof OperatorItemPair) {
                    throw placeholderError("UPDATE_TIME_LITERAL_PLACEHOLDER");
                }
                context.appendSetLeftItem(field, right); //  append left item
            } else {
                context.appendSetLeftItem(field, null); //  append left item
                //2. append operator
                if (this instanceof OperatorItemPair) {
                    ((OperatorItemPair) this).operator
                            .appendOperator(field, sqlBuilder, context);
                } else {
                    sqlBuilder.append(_Constant.SPACE_EQUAL);
                }
                //3. append right item
                ((_Expression) this.right).appendSql(sqlBuilder, context);
            }


        }


        @Override
        public final SqlField field() {
            return this.field;
        }

        @Override
        public final _Expression value() {
            return (_Expression) this.right;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append(this.field);
            if (this instanceof OperatorItemPair) {
                builder.append(((OperatorItemPair) this).operator);
            } else {
                builder.append(_Constant.SPACE_EQUAL);
            }
            builder.append(this.right);
            return builder.toString();
        }

        private CriteriaException placeholderError(final String name) {
            String m = String.format("SQLs.%s don't support %s", name, ((OperatorItemPair) this).operator.name());
            throw new CriteriaException(m);
        }

    }//FieldItemPair

    private static final class OperatorItemPair extends FieldItemPair {

        final AssignOperator operator;

        private OperatorItemPair(SqlField field, AssignOperator operator, ArmyExpression value) {
            super(field, value);
            this.operator = operator;
        }


    }//OperatorItemPair

    static final class RowItemPair extends ArmyItemPair implements _ItemPair._RowItemPair {

        final List<SqlField> fieldList;

        private RowItemPair(List<? extends SqlField> fieldList, SubQuery subQuery) {
            super(subQuery);
            final int selectionCount;
            selectionCount = ((_RowSet) subQuery).selectionSize();
            if (fieldList.size() != selectionCount) {
                String m = String.format("Row column count[%s] and selection count[%s] of SubQuery not match."
                        , fieldList.size(), selectionCount);
                throw new CriteriaException(m);
            }
            final List<SqlField> tempList = _Collections.arrayList(fieldList.size());
            for (SqlField field : fieldList) {
                if (!(field instanceof TableField)) {
                    tempList.add(field);
                    continue;
                }
                if (((TableField) field).updateMode() == UpdateMode.IMMUTABLE) {
                    throw _Exceptions.immutableField(field);
                }
                final String fieldName = field.fieldName();
                if (_MetaBridge.UPDATE_TIME.equals(fieldName) || _MetaBridge.VERSION.equals(fieldName)) {
                    throw _Exceptions.armyManageField((TableField) field);
                }
                tempList.add(field);
            }
            this.fieldList = Collections.unmodifiableList(tempList);
        }

        @Override
        public void appendItemPair(final StringBuilder sqlBuilder, final _SetClauseContext context) {
            final List<? extends SqlField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //1. append left paren
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(_Constant.SPACE_COMMA);
                }
                context.appendSetLeftItem(fieldList.get(i), null);
            }
            //3. append right paren
            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            sqlBuilder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            context.appendSubQuery((SubQuery) this.right);

        }

        @Override
        public List<? extends SqlField> rowFieldList() {
            return this.fieldList;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();

            //1. append left paren
            builder.append(_Constant.SPACE_LEFT_PAREN);
            final List<? extends SqlField> fieldList = this.fieldList;
            final int fieldSize = fieldList.size();
            //2. append field list
            for (int i = 0; i < fieldSize; i++) {
                if (i > 0) {
                    builder.append(_Constant.SPACE_COMMA);
                }
                builder.append(fieldList.get(i));
            }
            //3. append right paren
            builder.append(_Constant.SPACE_RIGHT_PAREN);

            //4. append '='
            builder.append(_Constant.SPACE_EQUAL);

            //5. append sub query
            builder.append(this.right);
            return builder.toString();
        }

    }//RowItemPair


    static final class CteImpl implements _Cte {

        final String name;

        final List<String> columnNameList;

        final SubStatement subStatement;

        private final _SelectionMap selectionMap;

        CteImpl(String name, SubStatement subStatement) {
            this(name, Collections.emptyList(), subStatement);
        }

        /**
         * @param columnNameList unmodified list
         */
        CteImpl(String name, List<String> columnNameList, SubStatement subStatement) {
            this.name = name;
            this.columnNameList = columnNameList;
            this.subStatement = subStatement;

            if (!(subStatement instanceof DerivedTable)) {
                throw CriteriaUtils.subDmlNoReturningClause(name);
            } else if (this.columnNameList.size() == 0) {
                this.selectionMap = (_DerivedTable) subStatement;
            } else {
                this.selectionMap = CriteriaUtils.createAliasSelectionMap(this.columnNameList,
                        ((_DerivedTable) subStatement).refAllSelection(), name);
            }

        }

        @Override
        public String name() {
            return this.name;
        }


        @Override
        public List<String> columnAliasList() {
            return this.columnNameList;
        }

        @Override
        public SubStatement subStatement() {
            return this.subStatement;
        }

        @Override
        public List<? extends Selection> refAllSelection() {
            return this.selectionMap.refAllSelection();
        }


        @Override
        public Selection refSelection(final String name) {
            return this.selectionMap.refSelection(name);
        }


    }//CteImpl




    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * *
     *
     * @see SQLs#DEFAULT
     */
    private static final class DefaultWord extends NonOperationExpression
            implements WordDefault, ArmyKeyWord {

        private DefaultWord() {
        }

        @Override
        public String spaceRender() {
            return _Constant.SPACE_DEFAULT;
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_DEFAULT);
        }

        @Override
        public String toString() {
            return _Constant.SPACE_DEFAULT;
        }


    }// DefaultWord

    /**
     * @see SQLs#_ASTERISK_EXP
     */
    private static final class LiteralSymbolAsterisk extends NonOperationExpression
            implements FunctionArg.SingleFunctionArg {

        private LiteralSymbolAsterisk() {
        }

        @Override
        public TypeMeta typeMeta() {
            throw unsupportedOperation(this);
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(" *");
        }

        @Override
        public String toString() {
            return " *";
        }


    }//LiteralSymbolAsterisk


}
