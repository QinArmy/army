package io.army.criteria.impl.inner.postgre;

import io.army.criteria.Selection;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.inner._Cte;
import io.army.criteria.impl.inner._SelfDescribed;
import io.army.lang.Nullable;

public interface _PostgreCte extends _Cte {

    @Nullable
    Postgres.WordMaterialized modifier();

    @Nullable
    _SearchClause searchClause();

    _CycleClause cycleClause();


    interface _SearchClause extends _SelfDescribed {

        Selection searchSeqSelection();


    }

    interface _CycleClause extends _SelfDescribed {

        Selection cycleMarkSelection();


        Selection cyclePathSelection();

    }



}
