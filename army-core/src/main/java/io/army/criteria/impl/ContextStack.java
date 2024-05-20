/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.impl;

import io.army.ArmyException;
import io.army.criteria.CriteriaException;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.mysql.inner._TabularBlock;
import io.army.util._Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>This class is the stack of context of {@link io.army.criteria.Statement}.
 * <p>Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 *
 * @since 0.6.0
 */
public abstract class ContextStack {

    private static final Logger LOG = LoggerFactory.getLogger(ContextStack.class);

    private ContextStack() {
        throw new UnsupportedOperationException();
    }

    private static final ThreadLocal<Stack> HOLDER = new ThreadLocal<>();


    public static CriteriaContext peek() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.getLast();

    }


    public static List<_TabularBlock> pop(final CriteriaContext context) {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }


        final List<_TabularBlock> blockList;
        // firstly , end context
        try {
            blockList = context.endContext();
        } catch (CriteriaException | Error e) {
            HOLDER.remove();
            throw e;
        } catch (Exception e) {
            HOLDER.remove();
            throw new CriteriaException("end statement context occur error", e);
        }

        // secondly , pop context
        if (context != stack.removeLast()) {
            HOLDER.remove();
            // no bug,never here
            String m = String.format("%s is not current context,context stack error", context);
            throw new CriteriaException(m);
        } else if (context.getOuterContext() == null) {
            assert stack.size() == 0;
            HOLDER.remove();
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace("pop {}", context);
        }
        return blockList;
    }

    public static void push(final CriteriaContext context) {
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
            HOLDER.remove();
            //no bug,never here
            String m = String.format("outer context[%s] and current[%s] not match,reject push",
                    outerContext, stack.peek());
            throw new IllegalArgumentException(m);
        }

    }

    public static CriteriaContext root() {
        final Stack stack = HOLDER.get();
        if (stack == null) {
            throw noContextStack();
        }
        return stack.getFirst();
    }


    public static CriteriaException criteriaError(CriteriaContext criteriaContext, Supplier<CriteriaException> supplier) {
        clearStackOnError(criteriaContext);
        return supplier.get();
    }

    @Deprecated
    public static CriteriaException criteriaError(Supplier<CriteriaException> supplier) {
        return supplier.get();
    }


    public static void assertNonNull(@Nullable Object obj) {
        if (obj == null) {
            HOLDER.remove();
            throw new NullPointerException();
        }
    }


    public static CriteriaException castCriteriaApi(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return _Exceptions.castCriteriaApi();
    }

    public static CriteriaException nonArmyExp(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return new CriteriaException(String.format("%s must be army expression", Expression.class.getName()));
    }

    public static NullPointerException nullPointer(CriteriaContext criteriaContext) {
        clearStackOnError(criteriaContext);
        return new NullPointerException();
    }

    public static CriteriaException clearStackAnd(Supplier<CriteriaException> supplier) {
        HOLDER.remove();
        return supplier.get();
    }

    public static CriteriaException clearStackAndCastCriteriaApi() {
        HOLDER.remove();
        return _Exceptions.castCriteriaApi();
    }

    public static <T, E extends ArmyException> E clearStackAnd(Function<T, E> function, @Nullable T input) {
        HOLDER.remove();
        return function.apply(input);
    }

    public static Error clearStackAndError(final Error cause) {
        HOLDER.remove();
        return cause;
    }

    public static <T, U, E extends ArmyException> E clearStackAnd(BiFunction<T, U, E> function, @Nullable T input1,
                                                           @Nullable U input2) {
        HOLDER.remove();
        return function.apply(input1, input2);
    }

    public static NullPointerException clearStackAndNullPointer() {
        HOLDER.remove();
        return new NullPointerException();
    }

    public static NullPointerException clearStackAndNullPointer(String msg) {
        HOLDER.remove();
        return new NullPointerException(msg);
    }

    public static CriteriaException clearStackAndCriteriaError(String msg) {
        HOLDER.remove();
        return new CriteriaException(msg);
    }

    public static CriteriaException clearStackAndCause(Exception cause, String msg) {
        HOLDER.remove();
        if (cause instanceof CriteriaException) {
            return (CriteriaException) cause;
        }
        return new CriteriaException(msg, cause);
    }


    public static RuntimeException clearStackAndNonArmyItem(final @Nullable Item exp) {
        HOLDER.remove();
        final RuntimeException e;
        if (exp == null) {
            e = new NullPointerException();
        } else {
            e = new CriteriaException(String.format("%s non-army item", exp));
        }
        return e;
    }


    public static <T> CriteriaException clearStackAndCriteriaError(Function<T, CriteriaException> function, T input) {
        final Stack stack = HOLDER.get();
        if (stack != null) {
            HOLDER.remove();
        }
        return function.apply(input);
    }

    public static <T, E extends CriteriaException> E criteriaError(final CriteriaContext criteriaContext,
                                                            Function<T, E> function, @Nullable T input) {
        clearStackOnError(criteriaContext);
        return function.apply(input);
    }

    public static <T, U> CriteriaException criteriaError(CriteriaContext criteriaContext,
                                                  BiFunction<T, U, CriteriaException> function,
                                                  @Nullable T input, @Nullable U input2) {
        clearStackOnError(criteriaContext);
        return function.apply(input, input2);
    }


    public static CriteriaException criteriaError(CriteriaContext criteriaContext, String message) {
        clearStackOnError(criteriaContext);
        return new CriteriaException(message);
    }

    @Deprecated
    public static CriteriaException criteriaError(String message) {
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
