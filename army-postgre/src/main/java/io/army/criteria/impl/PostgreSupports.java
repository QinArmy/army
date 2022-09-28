package io.army.criteria.impl;

import io.army.criteria.impl.inner._Cte;
import io.army.criteria.postgre.PostgreCteBuilder;

import java.util.List;
import java.util.function.BiConsumer;

abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }

    static PostgreCteBuilder cteBuilder(final boolean recursive, final CriteriaContext context
            , final BiConsumer<Boolean, List<_Cte>> withConsumer) {
        return new PostgreCteBuilderImpl(recursive, context, withConsumer);
    }


    private static final class PostgreCteBuilderImpl implements PostgreCteBuilder {

        private final boolean recursive;

        private final CriteriaContext context;

        private final BiConsumer<Boolean, List<_Cte>> withConsumer;

        private PostgreCteBuilderImpl(boolean recursive, CriteriaContext context
                , BiConsumer<Boolean, List<_Cte>> withConsumer) {
            this.recursive = recursive;
            this.context = context;
            this.withConsumer = withConsumer;
        }
    }//PostgreCteBuilderImpl


}
