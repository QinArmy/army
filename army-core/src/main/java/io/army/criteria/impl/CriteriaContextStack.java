package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.lang.Nullable;
import io.army.util._Exceptions;

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
        stack.pop(subContext);
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


    static CriteriaException criteriaError(CriteriaContext criteriaContext, Supplier<CriteriaException> supplier) {
        clearStackOnError(criteriaContext);
        return supplier.get();
    }

    @Deprecated
    static CriteriaException criteriaError(Supplier<CriteriaException> supplier) {
        return supplier.get();
    }


    @Deprecated
    static void assertNonNull(@Nullable Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }


    static void assertFunctionExp(CriteriaContext criteriaContext, @Nullable Expression expression) {
        if (!(expression instanceof ArmyExpression)) {
            clearStackOnError(criteriaContext);
            throw new CriteriaException("function must return non-null army expression");
        }
    }

    static CriteriaException castCriteriaApi(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return _Exceptions.castCriteriaApi();
    }

    static CriteriaException nonArmyExp(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return new CriteriaException(String.format("%s must be army expression", Expression.class.getName()));
    }

    static NullPointerException nullPointer(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return new NullPointerException();
    }

    static void assertNonNull(CriteriaContext criteriaContext, @Nullable Object obj, String message) {
        if (obj == null) {
            clearStackOnError(criteriaContext);
            throw new CriteriaException(message);
        }
    }

    @Deprecated
    static <T> CriteriaException criteriaError(Function<T, CriteriaException> function, T input) {
        return function.apply(input);
    }

    static <T> CriteriaException criteriaError(CriteriaContext criteriaContext, Function<T, CriteriaException> function
            , @Nullable T input) {
        clearStackOnError(criteriaContext);
        return function.apply(input);
    }


    static CriteriaException criteriaError(CriteriaContext criteriaContext, String message) {
        clearStackOnError(criteriaContext);
        return new CriteriaException(message);
    }

    @Deprecated
    static CriteriaException criteriaError(String message) {
        return new CriteriaException(message);
    }

    private static void clearStackOnError(CriteriaContext criteriaContext) {
        final Stack stack;
        stack = HOLDER.get();
        if (stack != null && stack.peek() == criteriaContext) {
            HOLDER.remove();
            stack.clearOnError();
        }

    }


    private interface Stack {

        void pop(CriteriaContext subContext);

        void push(CriteriaContext subContext);

        CriteriaContext peek();

        CriteriaContext rootContext();

        void clear(CriteriaContext rootContext);

        void clearOnError();
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
        public void pop(final CriteriaContext subContext) {
            final LinkedList<CriteriaContext> list = this.list;
            if (list.size() < 2) {
                throw new CriteriaException(String.format("No sub %s,reject pop.", CriteriaContext.class.getName()));
            }
            if (subContext != list.peekLast()) {
                String m = String.format("sub %s not match,reject pop.", CriteriaContext.class.getName());
                throw new CriteriaException(m);
            }
            list.pollLast();
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
                //no bug,never here
                throw new IllegalStateException("stack error");
            }
            return list.peekFirst();
        }

        @Override
        public void clear(final CriteriaContext rootContext) {
            final LinkedList<CriteriaContext> list = this.list;
            switch (list.size()) {
                case 0:
                    //no bug,never here
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

        @Override
        public void clearOnError() {
            this.list.clear();
        }


    }//ContextStack


}
