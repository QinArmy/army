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

package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.*;
import io.army.dialect.impl._Constant;
import io.army.dialect.impl._SqlContext;
import io.army.mapping.*;
import io.army.meta.TypeMeta;

import java.math.BigDecimal;
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


    public static final AscDesc ASC = SqlWords.KeyWordAscDesc.ASC;

    public static final AscDesc DESC = SqlWords.KeyWordAscDesc.DESC;


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

    public static final LiteralExpression LITERAL_100 = SQLs.literal(IntegerType.INSTANCE, 100);

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

    public static final ParamExpression PARAM_100 = SQLs.param(IntegerType.INSTANCE, 100);

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


    static final UnaryOperator<Select> SELECT_IDENTITY = Armies::identity;

    static final UnaryOperator<SubQuery> SUB_QUERY_IDENTITY = Armies::identity;

    static final Function<SubQuery, Expression> SCALAR_SUB_QUERY = Expressions::scalarExpression;

    static final UnaryOperator<Insert> INSERT_IDENTITY = Armies::identity;

    static final UnaryOperator<Insert> UPDATE_IDENTITY = Armies::identity;


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


    /**
     * <p>
     * This class representing sql {@code DEFAULT} key word.
     * *
     *
     * @see SQLs#DEFAULT
     */
    private static final class DefaultWord extends NonOperationExpression
            implements WordDefault, SqlWords.ArmyKeyWord {

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


    } // DefaultWord


}
