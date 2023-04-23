package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.*;
import io.army.criteria.postgre.*;
import io.army.mapping.MappingType;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is Postgre SQL syntax utils.
 * </p>
 *
 * @since 1.0
 */
public abstract class Postgres extends PostgreSyntax {


    /**
     * private constructor
     */
    private Postgres() {
    }


    public static final WordName NAME = PostgreWords.KeyWordName.NAME;

    public static final WordVersion VERSION = PostgreWords.KeyWordVersion.VERSION;

    public static final WordStandalone STANDALONE = PostgreWords.KeyWordStandalone.STANDALONE;

    public static final StandaloneOption YES = PostgreWords.KeyWordStandaloneOption.YES;

    public static final StandaloneOption NO = PostgreWords.KeyWordStandaloneOption.NO;

    public static final WordsNoValue NO_VALUE = PostgreWords.KeyWordsNoValue.NO_VALUE;

    public static final BooleanTestWord DOCUMENT = BooleanTestKeyWord.DOCUMENT;

    public static final WordPassing PASSING = PostgreWords.KeyWordPassing.PASSING;

    public static final PassingOption BY_REF = PostgreWords.WordPassingOption.BY_REF;

    public static final PassingOption BY_VALUE = PostgreWords.WordPassingOption.BY_VALUE;

    public static final NullOption NOT_NULL = SqlWords.KeyWordNotNull.NOT_NULL;

    public static final WordPath PATH = SqlWords.KeyWordPath.PATH;

    public static final WordsForOrdinality FOR_ORDINALITY = SqlWords.KeyWordsForOrdinality.FOR_ORDINALITY;


    /**
     * <p>
     * create single-table INSERT statement that is primary statement.
     * </p>
     */
    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.singleInsert();
    }

    /**
     * <p>
     * create SELECT statement that is primary statement.
     * </p>
     */
    public static PostgreQuery._WithSpec<Select> query() {
        return PostgreQueries.simpleQuery();
    }

    /**
     * <p>
     * create SUB-SELECT statement that is sub query statement.
     * </p>
     */
    public static PostgreQuery._WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }

    /**
     * <p>
     * create SUB-SELECT statement that is sub query statement and would be converted to {@link Expression}.
     * </p>
     */
    public static PostgreQuery._WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.simple();
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._BatchSingleWithSpec<BatchUpdate, BatchReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batchUpdate();
    }

    /**
     * <p>
     * create simple(non-batch) single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._SingleWithSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.simpleDelete();
    }

    /**
     * <p>
     * create batch single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._BatchSingleWithSpec<BatchDelete, BatchReturningDelete> batchSingleDelete() {
        return PostgreDeletes.batchDelete();
    }

    public static PostgreValues._WithSpec<Values> simpleValues() {
        return PostgreSimpleValues.simpleValues();
    }

    public static PostgreValues._WithSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(ContextStack.peek(), SQLs::_identity);
    }

    public interface _XmlNamedElementClause {

        _XmlNamedElementClause accept(Expression value, WordAs as, String name);

        _XmlNamedElementClause accept(BiFunction<MappingType, String, Expression> funcRef, String value, WordAs as, String name);

    }

    public interface _XmlNamedElementFieldClause extends _XmlNamedElementClause {

        _XmlNamedElementFieldClause accept(DataField field);

        _XmlNamedElementFieldClause accept(Expression value, WordAs as, String name);

        _XmlNamedElementFieldClause accept(BiFunction<MappingType, String, Expression> funcRef, String value, WordAs as, String name);

    }


    public interface _XmlTableColumnsClause {

        XmlTableCommaClause columns(String name, MappingType type, WordPath path, Expression columnExp, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, WordPath path, Expression columnExp, NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, WordPath path, Expression columnExp, WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause columns(String name, MappingType type, NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause columns(String name, MappingType type, WordPath path, Expression columnExp);

        XmlTableCommaClause columns(String name, MappingType type);

        XmlTableCommaClause columns(String name, WordsForOrdinality forOrdinality);


        XmlTableCommaClause columns(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);


        XmlTableCommaClause columns(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, WordDefault wordDefault, Expression defaultExp);


    }

    public interface XmlTableCommaClause {

        XmlTableCommaClause comma(String name, MappingType type, WordPath path, Expression columnExp, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, WordPath path, Expression columnExp, NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, WordPath path, Expression columnExp, WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause comma(String name, MappingType type, NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause comma(String name, MappingType type, WordPath path, Expression columnExp);

        XmlTableCommaClause comma(String name, MappingType type);

        XmlTableCommaClause comma(String name, WordsForOrdinality forOrdinality);


        XmlTableCommaClause comma(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, WordDefault wordDefault, Expression defaultExp, NullOption nullOption);


        XmlTableCommaClause comma(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, WordDefault wordDefault, Expression defaultExp);


    }


}
