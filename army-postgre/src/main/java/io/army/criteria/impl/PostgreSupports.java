package io.army.criteria.impl;

import io.army.criteria.impl.inner._Cte;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
import io.army.lang.Nullable;

import java.util.List;
import java.util.function.BiConsumer;

abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }

    static PostgreCteBuilder cteBuilder(final boolean recursive, final CriteriaContext context
            , final BiConsumer<Boolean, List<_Cte>> withConsumer) {
        return new PostgreCteBuilderImpl(recursive, context, withConsumer);
    }


    private static final class PostgreCteBuilderImpl implements PostgreCteBuilder, CteBuilderSpec {

        private final boolean recursive;
        private final CriteriaContext context;

        private final BiConsumer<Boolean, List<_Cte>> withConsumer;

        private final CriteriaContext.CteConsumer cteConsumer;

        private PostgreCteBuilderImpl(final boolean recursive, CriteriaContext context
                , BiConsumer<Boolean, List<_Cte>> withConsumer) {
            this.recursive = recursive;
            this.context = context;
            this.withConsumer = withConsumer;
            this.cteConsumer = context.onBeforeWithClause(recursive);

        }

        @Override
        public PostgreInsert._DynamicCteInsert<Void> cteInsert(final @Nullable String name) {
            if (name == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            return PostgreInserts.dynamicCteInsert(name, this.context, null);
        }

        @Override
        public <C> PostgreInsert._DynamicCteInsert<C> cteInsert(final @Nullable C criteria, final @Nullable String name) {
            if (criteria == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            } else if (name == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            return PostgreInserts.dynamicCteInsert(name, this.context, criteria);
        }


        @Override
        public void endWithClause(final boolean required) {
            final List<_Cte> cteList;
            cteList = this.cteConsumer.end();
            if (required && cteList.size() == 0) {
                throw CriteriaUtils.cteListIsEmpty(this.context);
            }
            this.withConsumer.accept(this.recursive, cteList);
        }


    }//PostgreCteBuilderImpl


}
