package io.army.criteria.impl;

import io.army.criteria.Clause;
import io.army.criteria.CriteriaException;
import io.army.criteria.Item;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

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


    static List<String> staticStringClause(final boolean required, @Nullable Consumer<Clause._StaticStringSpaceClause> consumer) {
        final StaticStringClause clause;
        clause = new StaticStringClause(required);
        return invokeConsumer(clause, consumer);
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


}
