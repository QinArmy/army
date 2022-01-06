package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;

import java.util.List;

public interface _MySQLQuery extends _Query, _DialectStatement {

    boolean groupByWithRollUp();

    @Nullable
    IntoClause intoClause();

    interface IntoClause {

        IntoPosition position();

        List<String> intoList();
    }

    enum IntoPosition {
        BEFORE_FROM,
        END
    }


}
