package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class ClauseUtils {

    private ClauseUtils() {
        throw new UnsupportedOperationException();
    }


    static <T> void invokingDynamicConsumer(final boolean required, final List<T> list, final boolean nonNull,
                                            @Nullable Consumer<Consumer<T>> consumer) {
        if (consumer == null) {
            throw CriteriaUtils.consumerIsNull();
        }

        try {

            final int startSize = list.size();

            consumer.accept(item -> {
                if (item == null && nonNull) {
                    throw ContextStack.clearStackAndNullPointer();
                }
                list.add(item);
            });

            if (required && list.size() == startSize) {
                throw CriteriaUtils.dontAddAnyItem();
            }
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }


    /**
     * @return a unmodified list
     */
    static <T> List<T> invokingDynamicConsumer(boolean required, boolean nonNull, Consumer<Consumer<T>> consumer) {
        final List<T> list = _Collections.arrayList();
        invokingDynamicConsumer(required, list, nonNull, consumer);
        return _Collections.unmodifiableList(list);
    }

    static <T, C extends ArmyAcceptClause<T>> List<T> invokeConsumer(final C clause, final @Nullable Consumer<? super C> consumer) {
        if (consumer == null) {
            throw CriteriaUtils.consumerIsNull();
        }
        try {
            consumer.accept(clause);
            return clause.endClause();
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }

    static List<_Expression> invokeDynamicExpressionClause(final boolean required, final boolean nonNull,
                                                           final @Nullable Consumer<Consumer<Expression>> consumer) {
        if (consumer == null) {
            throw CriteriaUtils.consumerIsNull();
        }
        final List<_Expression> list = _Collections.arrayList();

        final Consumer<Expression> clause;
        clause = exp -> {
            if (exp == null) {
                if (nonNull) {
                    throw ContextStack.clearStackAndNonArmyItem(null);
                }
            } else if (!(exp instanceof ArmyExpression)) {
                throw ContextStack.clearStackAndNonArmyItem(exp);
            }
            list.add((ArmyExpression) exp);
        };

        CriteriaUtils.invokeConsumer(clause, consumer);

        if (required && list.size() == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        }
        return _Collections.unmodifiableList(list);
    }


    static List<String> staticStringClause(final boolean required, @Nullable Consumer<Clause._StaticStringSpaceClause> consumer) {
        final StaticStringClause clause;
        clause = new StaticStringClause(required);
        return invokeConsumer(clause, consumer);
    }


    @SuppressWarnings("unchecked")
    static List<_Expression> invokeStaticExpressionClause(boolean required, Consumer<Clause._VariadicExprSpaceClause> consumer) {
        final VariadicExpressionClause clause = new VariadicExpressionClause(required, null, null);
        CriteriaUtils.invokeConsumer(clause, consumer);
        return (List<_Expression>) clause.endClause();
    }

    static VariadicExpressionClause variadicExpressionClause(boolean required, @Nullable SQLWords separator, ArrayList<Object> expList) {
        return new VariadicExpressionClause(required, separator, expList);
    }


    static List<?> endSingleClause(final boolean required, final int startLength, @Nullable List<Object> expList,
                                   final Consumer<List<Object>> setter) {
        if (expList == null) {
            if (required) {
                throw CriteriaUtils.dontAddAnyItem();
            }
            expList = Collections.emptyList();
        } else if (required && (expList.size() - startLength) == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        } else if (expList instanceof ArrayList) {
            expList = _Collections.unmodifiableList(expList);
        } else {
            throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
        }

        setter.accept(expList);
        return expList;
    }

    static <T> T invokeSupplier(final @Nullable Supplier<T> supplier) {
        if (supplier == null) {
            throw ContextStack.clearStackAndNullPointer("Supplier is null");
        }
        try {

            final T result;
            result = supplier.get();
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer("Supplier must return non-null.");
            }
            return result;
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }

    static boolean invokeBooleanSupplier(final @Nullable BooleanSupplier supplier) {
        if (supplier == null) {
            throw ContextStack.clearStackAndNullPointer("Supplier is null");
        }
        try {
            return supplier.getAsBoolean();
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }

    static <T, R> R invokeFunction(final @Nullable Function<T, R> function, final T data) {
        try {
            if (function == null) {
                throw new NullPointerException("java.util.function.Function is null,couldn't be invoked");
            }
            final R result;
            result = function.apply(data);
            if (result == null) {
                throw new NullPointerException("function must return non-null");
            }
            return result;
        } catch (CriteriaException e) {
            throw ContextStack.clearStackAndCause(e, e.getMessage());
        } catch (Exception e) {
            throw ContextStack.clearStackAnd(CriteriaException::new, e);
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
    }


    private static final class StaticStringClause implements Clause._StaticStringSpaceClause,
            Clause._StaticStringDualCommaClause, Clause._StaticStringQuadraCommaClause,
            ArmyAcceptClause<String> {

        private final boolean required;

        private List<String> list;

        private StaticStringClause(boolean required) {
            this.required = required;
        }


        @Override
        public Item space(String str1) {
            return this.comma(str1);
        }

        @Override
        public Clause._StaticStringDualCommaClause space(String str1, String str2) {
            return this.comma(str1, str2);
        }

        @Override
        public Item space(String str1, String str2, String str3) {
            return this.comma(str1, str2, str3);
        }

        @Override
        public Clause._StaticStringQuadraCommaClause space(String str1, String str2, String str3, String str4) {
            return this.comma(str1, str2, str3, str4);
        }

        @Override
        public Item comma(@Nullable String str1) {
            addString(str1);
            return this;
        }

        @Override
        public Clause._StaticStringDualCommaClause comma(String str1, String str2) {
            addString(str1);
            addString(str2);
            return this;
        }

        @Override
        public Item comma(String str1, String str2, String str3) {
            addString(str1);
            addString(str2);
            addString(str3);
            return this;
        }

        @Override
        public Clause._StaticStringQuadraCommaClause comma(String str1, String str2, String str3, String str4) {
            addString(str1);
            addString(str2);
            addString(str3);
            addString(str4);
            return this;
        }

        private void addString(final @Nullable String str) {
            if (str == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            List<String> stringList = this.list;
            if (stringList == null) {
                this.list = stringList = _Collections.arrayList();
            } else if (!(stringList instanceof ArrayList)) {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            stringList.add(str);
        }

        @Override
        public List<String> endClause() {
            List<String> stringList = this.list;
            if (stringList == null) {
                if (this.required) {
                    throw CriteriaUtils.dontAddAnyItem();
                }
                this.list = stringList = Collections.emptyList();
            } else if (stringList instanceof ArrayList) {
                this.list = stringList = _Collections.unmodifiableList(stringList);
            } else {
                throw ContextStack.clearStackAndCastCriteriaApi();
            }
            return stringList;
        }


    } // StaticStringClause


    static final class VariadicExpressionClause implements Clause._VariadicExprSpaceClause,
            Clause._VariadicExprCommaClause {

        private final boolean required;

        private final SQLWords separator;

        private final int startLength;

        private List<Object> expList;

        private VariadicExpressionClause(boolean required, @Nullable SQLWords separator, @Nullable ArrayList<Object> expList) {
            this.required = required;
            this.separator = separator;
            this.expList = expList;

            if (expList == null) {
                this.startLength = 0;
            } else {
                this.startLength = expList.size();
            }
        }

        @Override
        public Clause._VariadicExprCommaClause space(Expression exp) {
            return comma(exp);
        }

        @Override
        public Clause._VariadicExprCommaClause space(Expression exp1, Expression exp2) {
            comma(exp1);
            return comma(exp2);
        }

        @Override
        public Clause._VariadicExprCommaClause space(Expression exp1, Expression exp2, Expression exp3) {
            comma(exp1);
            comma(exp2);
            return comma(exp3);
        }

        @Override
        public Clause._VariadicExprCommaClause space(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
            comma(exp1);
            comma(exp2);
            comma(exp3);
            return comma(exp4);
        }

        @Override
        public Clause._VariadicExprCommaClause comma(final @Nullable Expression exp) {
            List<Object> expList = this.expList;
            if (expList == null) {
                this.expList = expList = _Collections.arrayList();
            } else if (!(expList instanceof ArrayList)) {
                throw ContextStack.clearStackAnd(_Exceptions::castCriteriaApi);
            }

            final SQLWords separator = this.separator;
            if (separator != null && expList.size() > this.startLength) {
                expList.add(separator);
            }
            if (!(exp instanceof ArmyExpression)) {
                throw ContextStack.clearStackAndNonArmyItem(exp);
            }
            expList.add(exp);
            return this;
        }

        @Override
        public Clause._VariadicExprCommaClause comma(Expression exp1, Expression exp2) {
            comma(exp1);
            return comma(exp2);
        }

        @Override
        public Clause._VariadicExprCommaClause comma(Expression exp1, Expression exp2, Expression exp3) {
            comma(exp1);
            comma(exp2);
            return comma(exp3);
        }

        @Override
        public Clause._VariadicExprCommaClause comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4) {
            comma(exp1);
            comma(exp2);
            comma(exp3);
            return comma(exp4);
        }

        List<?> endClause() {
            return endSingleClause(this.required, this.startLength, this.expList, this::setExpList);
        }


        private void setExpList(List<Object> expList) {
            this.expList = expList;
        }


    } // VariadicExpressionClause


}
