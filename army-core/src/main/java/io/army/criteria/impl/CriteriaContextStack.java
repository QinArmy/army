package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.lang.Nullable;

import java.util.LinkedList;
import java.util.Objects;


abstract class CriteriaContextStack {

    private static final ThreadLocal<Stack> HOLDER = new ThreadLocal<>();

    private CriteriaContextStack() {
        throw new UnsupportedOperationException();
    }


    static void setContextStack(CriteriaContext rootContext) {
        HOLDER.set(new ContextStack(rootContext));
    }

    static void pop(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw notContextStack();
        }
        final CriteriaContext context = stack.peek();
        if (subContext != context) {
            String m;
            m = "Current context and the context of sub query ( or with clause ) not match,reject pop.";
            throw new CriteriaException(m);
        }
        stack.pop().clear();
    }

    static void push(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw notContextStack();
        }
        stack.push(subContext);
    }

    @Nullable
    static <C> C getCriteria() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw notContextStack();
        }
        return stack.rootContext().criteria();
    }

    static void clearContextStack(final CriteriaContext rootContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw notContextStack();
        }
        stack.clear(rootContext);
        HOLDER.remove();
    }

    private static CriteriaException notContextStack() {
        String m;
        m = "Not found any primary query context, so context stack of sub query ( or with clause ) have cleared.";
        return new CriteriaException(m);
    }


    static CriteriaException criteriaInstanceNotMatch() {
        String m;
        m = "Criteria instance and current context criteria instance not match,couldn't create sub query or with clause.";
        return new CriteriaException(m);
    }


    private interface Stack {

        CriteriaContext pop();

        void push(CriteriaContext subContext);

        CriteriaContext peek();

        CriteriaContext rootContext();

        void clear(CriteriaContext rootContext);
    }

    private static final class ContextStack implements Stack {

        private final LinkedList<CriteriaContext> list;

        private ContextStack(final CriteriaContext rootContext) {
            Objects.requireNonNull(rootContext);
            final LinkedList<CriteriaContext> list = new LinkedList<>();
            list.push(rootContext);
            this.list = list;
        }

        @Override
        public CriteriaContext pop() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() < 2) {
                throw new CriteriaException("Sub query create error");
            }
            return list.pop();
        }

        @Override
        public void push(final CriteriaContext subContext) {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() == 0) {
                throw new IllegalStateException("stack error");
            }
            final CriteriaContext rootContext = list.get(0);
            if (subContext.criteria() != rootContext.criteria()) {
                throw criteriaInstanceNotMatch();
            }
            list.push(subContext);
        }

        @Override
        public CriteriaContext peek() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() < 2) {
                throw new CriteriaException("Not found any sub context.");
            }
            return list.peek();
        }

        @Override
        public CriteriaContext rootContext() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() == 0) {
                throw new IllegalStateException("stack error");
            }
            return list.get(0);
        }

        @Override
        public void clear(final CriteriaContext rootContext) {
            final LinkedList<CriteriaContext> list = this.list;
            switch (list.size()) {
                case 0:
                    throw new IllegalStateException("stack error");
                case 1: {
                    if (rootContext != list.get(0)) {
                        throw new CriteriaException("Root context not match,reject clear context stack.");
                    }
                    list.clear();
                }
                break;
                default: {
                    throw new CriteriaException("Exists sub context,reject clear context stack.");
                }
            }

        }


    }


}
