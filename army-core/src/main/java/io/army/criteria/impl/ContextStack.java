package io.army.criteria.impl;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.Item;

import javax.annotation.Nullable;

import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This class is the stack of context of {@link io.army.criteria.Statement}.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
abstract class ContextStack {

    private static final Logger LOG = LoggerFactory.getLogger(ContextStack.class);

    private ContextStack() {
        throw new UnsupportedOperationException();
    }

    private static final ThreadLocal<Stack> HOLDER = new ThreadLocal<>();


    static CriteriaContext peek() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.getLast();

    }


    static CriteriaContext pop(final CriteriaContext context) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        final CriteriaContext currentContext;
        currentContext = stack.getLast();
        if (context != currentContext) {
            // no bug,never here
            String m = String.format("%s and current %s not match,reject pop.", context, currentContext);
            throw new IllegalArgumentException(m);
        } else if (context.getOuterContext() == null) {
            assert stack.size() == 1;
            HOLDER.remove();
            stack.clear();
        } else if (stack.removeLast() != currentContext) {
            // Stack no bug,never here
            throw new IllegalStateException("stack state error");
        }
        context.contextEndEvent();
        if (LOG.isTraceEnabled()) {
            LOG.trace("pop {}", context);
        }
        return context;
    }

    static void push(final CriteriaContext context) {
        final CriteriaContext outerContext;
        outerContext = context.getOuterContext();
        final Stack stack;
        if (outerContext == null) {
            //reset stack
            HOLDER.set(new Stack(context));
            if (LOG.isTraceEnabled()) {
                LOG.trace("reset stack for primary context {}.", context);
            }
        } else if ((stack = HOLDER.get()) == null) {
            //no bug,never here
            throw new IllegalArgumentException("exists outer context,but no stack.");
        } else if (outerContext == stack.getLast()) {
            stack.addLast(context);
            if (LOG.isTraceEnabled()) {
                LOG.trace("push {}", context);
            }
        } else {
            //no bug,never here
            String m = String.format("outer context[%s] and current[%s] not match,reject push",
                    outerContext, stack.peek());
            throw new IllegalArgumentException(m);
        }

    }

    static CriteriaContext root() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.getFirst();
    }

    static boolean isEmpty() {
        final Stack stack = HOLDER.get();
        return stack == null || stack.size() == 0;
    }

    static void addEndLise(final Runnable listener, final Supplier<CriteriaException> errorHandler) {
        final Stack stack;
        if ((stack = HOLDER.get()) == null) {
            throw errorHandler.get();
        }
        stack.getLast().addEndEventListener(listener);
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
            final Stack stack = HOLDER.get();
            if (stack != null) {
                HOLDER.remove();
                stack.clear();
            }
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

    static CriteriaException clearStackAnd(Supplier<CriteriaException> supplier) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return supplier.get();
    }

    static <T, E extends ArmyException> E clearStackAnd(Function<T, E> function, @Nullable T input) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return function.apply(input);
    }

    static <T, U, E extends CriteriaException> E clearStackAnd(BiFunction<T, U, E> function, @Nullable T input1,
                                                               @Nullable U input2) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return function.apply(input1, input2);
    }

    static NullPointerException clearStackAndNullPointer() {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
            stack.clear();
        }
        return new NullPointerException();
    }

    static CriteriaException clearStackAndCriteriaError(String msg) {
        final Stack stack;
        if ((stack = HOLDER.get()) != null) {
            HOLDER.remove();
            stack.clear();
        }
        return new CriteriaException(msg);
    }


    static RuntimeException clearStackAndNonArmyItem(final @Nullable Item exp) {
        final Stack stack;
        if ((stack = HOLDER.get()) != null) {
            HOLDER.remove();
            stack.clear();
        }
        final RuntimeException e;
        if (exp == null) {
            e = new NullPointerException();
        } else {
            e = new CriteriaException(String.format("%s non-army item", exp));
        }
        return e;
    }


    static <T> CriteriaException clearStackAndCriteriaError(Function<T, CriteriaException> function, T input) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return function.apply(input);
    }

    static <T, E extends CriteriaException> E criteriaError(final CriteriaContext criteriaContext,
                                                            Function<T, E> function, @Nullable T input) {
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
        if (stack != null && stack.peekLast() == criteriaContext) {
            HOLDER.remove();
            stack.clear();
        }

    }


    private static CriteriaException noContextStack() {
        String m;
        m = "Not found any primary query context, so context stack of sub query ( or with clause ) have cleared.";
        return new CriteriaException(m);
    }


    private static final class Stack extends LinkedList<CriteriaContext> {

        private Stack(CriteriaContext root) {
            this.addLast(root);
        }

    }//Stack


}
