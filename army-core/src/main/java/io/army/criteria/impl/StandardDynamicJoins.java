package io.army.criteria.impl;

import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.standard.StandardCrosses;
import io.army.criteria.standard.StandardJoins;

import java.util.function.Consumer;

abstract class StandardDynamicJoins {

    static StandardJoins joinBuilder(CriteriaContext context, _JoinType joinTyp
            , Consumer<_TableBlock> blockConsumer) {
        throw new UnsupportedOperationException();
    }

    static StandardCrosses crossBuilder(CriteriaContext context, Consumer<_TableBlock> blockConsumer) {
        throw new UnsupportedOperationException();
    }


}
