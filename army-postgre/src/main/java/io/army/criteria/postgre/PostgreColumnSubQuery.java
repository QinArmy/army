package io.army.criteria.postgre;

public interface PostgreColumnSubQuery<E, C> extends PostgreSubQuery<C> {


    interface PostgreSubQueryAble<E, C> extends SubQueryAble<C>, PostgreSubQuerySQLAble {

        PostgreColumnSubQuery<E, C> asSubQuery();
    }

    interface PostgreRowSubQuerySelectPartAble<C> extends PostgreSubQuerySelectPartAble<C> {


    }


    interface PostgreRowSubQueryFromAble<C> extends PostgreSubQueryFromAble<C> {

    }


    interface PostgreRowSubQueryJoinAble<C> extends PostgreSubQueryJoinAble<C> {


    }


    interface PostgreRowSubQueryTableSampleOnAble<C> extends PostgreSubQueryTableSampleOnAble<C> {


    }

    interface PostgreRowSubQueryOnAble<C> extends PostgreSubQueryOnAble<C> {


    }

    interface PostgreRowSubQueryWhereAble<C> extends PostgreSubQueryWhereAble<C> {

    }


    interface PostgreRowSubQueryWhereAndAble<C> extends PostgreSubQueryWhereAndAble<C> {


    }

    interface PostgreRowSubQueryGroupByAble<C> extends PostgreSubQueryGroupByAble<C> {

    }


    interface PostgreRowSubQueryHavingAble<C> extends PostgreSubQueryHavingAble<C> {


    }

    interface PostgreRowSubQueryWindowAble<C> extends PostgreSubQueryWindowAble<C> {

    }


    interface PostgreRowSubQueryOrderByAble<C> extends PostgreSubQueryOrderByAble<C> {


    }

    interface PostgreRowSubQueryLimitAble<C> extends PostgreSubQueryLimitAble<C> {


    }

}
