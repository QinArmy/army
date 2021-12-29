package io.army.criteria.mysql;

import io.army.criteria.*;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface MySQLQuery extends Query, DialectStatement {


    interface MySQLSelectPartSpec<C, F> extends StandardSelectClauseSpec<C, F> {

        <S extends SelectPart> F select(List<Hint> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

        <S extends SelectPart> F select(Function<C, List<Hint>> hints, List<SQLModifier> modifiers, Function<C, List<S>> function);

    }


    interface IndexHintClause<C, S> {

        IndexHintWordClause<S> use();

        IndexHintWordClause<S> ignore();

        IndexHintWordClause<S> force();

        /**
         * @return clause , clause no action if predicate return false.
         */
        IndexHintWordClause<S> ifUse(Predicate<C> predicate);


        /**
         * @return clause , clause no action if predicate return false.
         */
        IndexHintWordClause<S> ifIgnore(Predicate<C> predicate);

        /**
         * @return clause , clause no action if predicate return false.
         */
        IndexHintWordClause<S> ifForce(Predicate<C> predicate);

    }

    interface IndexHintWordClause<S> {

        IndexHintPurpose57Spec<S> index();

        IndexHintPurpose57Spec<S> key();

        S index(List<String> indexNameList);

        S key(List<String> indexNameList);
    }


    interface IndexHintPurpose57Spec<S> {

        S froJoin(List<String> indexNameList);

        S froOrderBy(List<String> indexNameList);

        S froGroupBy(List<String> indexNameList);

    }


}
