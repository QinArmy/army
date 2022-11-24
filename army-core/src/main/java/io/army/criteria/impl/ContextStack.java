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


    static CriteriaContext peek() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.peek();

    }


    static CriteriaContext pop(final CriteriaContext context) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        if (context.getOuterContext() == null) {
            stack.clear(context);
            HOLDER.remove();
        } else {
            stack.pop(context);
        }
        context.contextEndEvent();
        if (LOG.isTraceEnabled()) {
            LOG.trace("pop {},hash:{}", context.getClass().getName(), System.identityHashCode(context));
        }
        return context;
    }

    static void push(final CriteriaContext context) {
        final CriteriaContext outerContext;
        outerContext = context.getOuterContext();

        final Stack stack;
        if (outerContext == null) {
            //reset
            HOLDER.set(new ArmyContextStack(context));
            if (LOG.isTraceEnabled()) {
                LOG.trace("reset stack for primary context {},hash:{}", context.getClass().getName()
                        , System.identityHashCode(context));
            }
        } else if ((stack = HOLDER.get()) == null) {
            //no bug,never here
            throw new IllegalArgumentException("exists outer context,but no stack.");
        } else if (outerContext == stack.peek()) {
            stack.push(context);
            if (LOG.isTraceEnabled()) {
                LOG.trace("push {},hash:{}", context.getClass().getName(), System.identityHashCode(context));
            }
        } else {
            //no bug,never here
            throw new IllegalArgumentException("outer context not match,reject push");
        }

    }

    static CriteriaContext root() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.rootContext();
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

    static NullPointerException clearStackAndNullPointer() {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return new NullPointerException();
    }

    static CriteriaException clearStackAndCriteriaError(String msg) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return new CriteriaException(msg);
    }


    static <T> CriteriaException clearStackAndCriteriaError(Function<T, CriteriaException> function, T input) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return function.apply(input);
    }

    static <T> CriteriaException criteriaError(CriteriaContext criteriaContext,
                                               Function<T, CriteriaException> function, @Nullable T input) {
        clearStackOnError(criteriaContext);
        return function.apply(input);
    }

    static <T, U> CriteriaException criteriaError(CriteriaContext criteriaContext,
                                                  BiFunction<T, U, CriteriaException> function,
                                                  @Nullable T input, @Nullable U input2) {
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


    private static CriteriaException noContextStack() {
        String m;
        m = "Not found any primary query context, so context stack of sub query ( or with clause ) have cleared.";
        return new CriteriaException(m);
    }


    private interface Stack {

        void pop(CriteriaContext subContext);

        void push(CriteriaContext subContext);


        boolean isEmpty();

        CriteriaContext peek();

        CriteriaContext rootContext();

        void clear(CriteriaContext rootContext);

        void clearOnError();
    }

    private static final class ArmyContextStack implements Stack {

        private final LinkedList<CriteriaContext> list;

        private ArmyContextStack(final CriteriaContext rootContext) {
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
        public boolean isEmpty() {
            return this.list.size() == 0;
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
