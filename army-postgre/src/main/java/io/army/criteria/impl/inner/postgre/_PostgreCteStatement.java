package io.army.criteria.impl.inner.postgre;

import io.army.criteria.SQLWords;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner._Expression;

import javax.annotation.Nullable;

import java.util.List;

public interface _PostgreCteStatement extends SubStatement {

    @Nullable
    SQLWords materializedOption();

    SubStatement subStatement();


    interface _SearchOptionClauseSpec extends _PostgreCteStatement {

        @Nullable
        SQLWords searchOption();

        List<String> firstByList();

        String searchSeqColumnName();

        @Nullable
        List<String> cycleColumnList();

        String cycleMarkColumnName();

        @Nullable
        _Expression cycleMarkValue();

        @Nullable
        _Expression cycleMarkDefault();

        String cyclePathColumnName();

    }



}
