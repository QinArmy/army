package io.army.criteria.impl;

import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.lang.Nullable;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.BiFunction;
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
abstract class ContextStack {

    private static final Logger LOG = LoggerFactory.getLogger(io.army.criteria.impl.ContextStack.class);

    private ContextStack() {
        throw new UnsupportedOperationException();
    }

    private static final ThreadLocal<Stack> HOLDER = new ThreadLocal<>();


    static void setContextStack(CriteriaContext rootContext) {
        HOLDER.set(new ContextStack(rootContext));
        if (LOG.isTraceEnabled()) {
            LOG.trace("setContextStack {},hash:{}", rootContext.getClass().getName()
                    , System.identityHashCode(rootContext));
        }
    }

    static void clearContextStack(final CriteriaContext rootContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        stack.clear(rootContext);
        HOLDER.remove();
        rootContext.contextEnd();
        if (LOG.isTraceEnabled()) {
            LOG.trace("clearContextStack {},hash:{}", rootContext.getClass().getName()
                    , System.identityHashCode(rootContext));
        }

    }

    static CriteriaContext peek() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.peek();

    }

    static CriteriaContext peek(final @Nullable Object criteria) {
        final Stack stack = HOLDER.get();
        final CriteriaContext currentContext;
        if (stack == null) {
            currentContext = null;
        } else {
            currentContext = stack.peek();
        }

        if (criteria == null) {
            if (currentContext != null) {
                HOLDER.remove();
            }
            throw new NullPointerException("criteria must be non-null");
        }
        if (currentContext == null) {
            throw noContextStack();
        }
        return currentContext;
    }

    static void pop(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        stack.pop(subContext);
        subContext.contextEnd();
        if (LOG.isTraceEnabled()) {
            LOG.trace("pop {},hash:{}", subContext.getClass().getName(), System.identityHashCode(subContext));
        }

    }

    static void push(final CriteriaContext subContext) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        stack.push(subContext);
        if (LOG.isTraceEnabled()) {
            LOG.trace("push {},hash:{}", subContext.getClass().getName(), System.identityHashCode(subContext));
        }
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

    static <C> CriteriaContext getCurrentContext(final @Nullable C criteria) {
        final CriteriaContext currentContext;
        currentContext = io.army.criteria.impl.ContextStack.peek();
        if (criteria != currentContext.criteria()) {
            throw CriteriaUtils.criteriaNotMatch(currentContext);
        }
        return currentContext;
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


    static void assertNonNull(@Nullable Object obj) {
        if (obj == null) {
            clearStackOnError(io.army.criteria.impl.ContextStack.peek());
            throw new NullPointerException();
        }
    }

    @Deprecated
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


    @Deprecated
    static <T> CriteriaException criteriaError(Function<T, CriteriaException> function, T input) {
        return function.apply(input);
    }

    static <T> CriteriaException criteriaError(CriteriaContext criteriaContext, Function<T, CriteriaException> function
            , @Nullable T input) {
        clearStackOnError(criteriaContext);
        return function.apply(input);
    }

    static <T, U> CriteriaException criteriaError(CriteriaContext criteriaContext, BiFunction<T, U, CriteriaException> function
            , @Nullable T input, @Nullable U input2) {
        clearStackOnError(criteriaContext);
        return function.apply(input, input2);
    }


    static CriteriaException criteriaError(CriteriaContext criteriaContext, String message) {
        clearStackOnError(criteriaContext);
        return new CriteriaException(message);
    }

    @Deprecated
    static CriteriaException criteriaError(String message) {
        return new CriteriaException(message);
    }

    private static void clearStackOnError(final CriteriaContext criteriaContext) {
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