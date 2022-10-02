package io.army.criteria.impl;

import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreInsert;
import io.army.lang.Nullable;

abstract class PostgreSupports extends CriteriaSupports {


    private PostgreSupports() {
    }

    static PostgreCteBuilder cteBuilder(final boolean recursive, final CriteriaContext context) {
        return new PostgreCteBuilderImpl(recursive, context);
    }


    private static final class PostgreCteBuilderImpl implements PostgreCteBuilder {

        private final boolean recursive;

        private final CriteriaContext context;

        private PostgreCteBuilderImpl(final boolean recursive, CriteriaContext context) {
            this.recursive = recursive;
            this.context = context;
            context.onBeforeWithClause(recursive);
        }

        @Override
        public PostgreInsert._DynamicSubInsert<Void> cteInsert(final @Nullable String name) {
            if (name == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            return PostgreInserts.dynamicSubInsert(name, this.context, null);
        }

        @Override
        public <C> PostgreInsert._DynamicSubInsert<C> cteInsert(final @Nullable C criteria, final @Nullable String name) {
            if (criteria == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            } else if (name == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            return PostgreInserts.dynamicSubInsert(name, this.context, criteria);
        }

        @Override
        public boolean isRecursive() {
            return this.recursive;
        }


    }//PostgreCteBuilderImpl


}
