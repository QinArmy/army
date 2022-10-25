package io.army.criteria.impl;

import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.postgre.PostgreCrosses;
import io.army.criteria.postgre.PostgreJoins;

import java.util.function.Consumer;

abstract class PostgreDynamicJoins {

    static PostgreJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TableBlock> blockConsumer) {
        throw new UnsupportedOperationException();
    }

    static PostgreCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        throw new UnsupportedOperationException();
    }


}
