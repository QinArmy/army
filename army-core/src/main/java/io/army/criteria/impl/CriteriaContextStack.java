package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.lang.Nullable;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class CriteriaContextStack {

    private CriteriaContextStack() {
        throw new UnsupportedOperationException();
    }

    private static final ThreadLocal<Stack> HOLDER = new ThreadLocal<>();


    static void setContextStack(CriteriaContext rootContext) {
        HOLDER.set(new ContextStack(rootContext));
    }

    static CriteriaContext peek() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.peek();

    }

    static void pop(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        final CriteriaContext context = stack.peek();
        if (subContext != context) {
            String m;
            m = "Current context and the context of sub query ( or with clause ) not match,reject pop.";
            throw new CriteriaException(m);
        }
        stack.pop();
    }

    static void push(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        stack.push(subContext);
    }

    static CriteriaContext root() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.rootContext();
    }

    @Nullable
    static <C> C getTopCriteria() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.peek().criteria();
    }

    static void clearContextStack(final CriteriaContext rootContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        stack.clear(rootContext);
        HOLDER.remove();
    }

    private static CriteriaException noContextStack() {
        String m;
        m = "Not found any primary query context, so context stack of sub query ( or with clause ) have cleared.";
        return new CriteriaException(m);
    }


    static CriteriaException criteriaInstanceNotMatch() {
        String m;
        m = "Criteria instance and current context criteria instance not match,couldn't create sub query or with clause.";
        return new CriteriaException(m);
    }


    static CriteriaException criteriaError(Supplier<CriteriaException> supplier) {
        return supplier.get();
    }


    static void assertNonNull(@Nullable Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }

    static void assertTrue(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    static void assertNonNull(@Nullable Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }

    static <T> CriteriaException criteriaError(Function<T, CriteriaException> function, T input) {
        return function.apply(input);
    }

    static CriteriaException criteriaError(String message) {
        return new CriteriaException(message);
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
            list.addLast(rootContext);
            this.list = list;
        }

        @Override
        public CriteriaContext pop() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() < 2) {
                throw new CriteriaException("Sub query create error");
            }
            return list.pollLast();
        }

        @Override
        public void push(final CriteriaContext subContext) {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() == 0) {
                throw new IllegalStateException("stack error");
            }
            list.addLast(subContext);
        }

        @Override
        public CriteriaContext peek() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() == 0) {
                throw new CriteriaException("Not found any context.");
            }
            return list.peekLast();
        }

        @Override
        public CriteriaContext rootContext() {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() == 0) {
                throw new IllegalStateException("stack error");
            }
            return list.peekFirst();
        }

        @Override
        public void clear(final CriteriaContext rootContext) {
            final LinkedList<CriteriaContext> list = this.list;
            switch (list.size()) {
                case 0:
                    throw new IllegalStateException("stack error");
                case 1: {
                    if (rootContext != list.peekFirst()) {
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
