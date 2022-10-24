package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.SubStatement;
import io.army.lang.Nullable;

public interface _PostgreCteStatement extends SubStatement {

    @Nullable
    SQLWords materializedOption();

    SubStatement subStatement();


    interface _SearchOptionClauseSpec {


    }

    interface _CycleOptionClauseSpec {

    }


}
