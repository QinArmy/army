package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.util._CollectionUtils;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

abstract class MySQLPartitionClause<C, PR> implements Statement._LeftParenStringQuadraOptionalSpec<C, PR>
        , Statement._CommaStringDualSpec<PR>, Statement._CommaStringQuadraSpec<PR> {

    static <C, PR> Statement._LeftParenStringQuadraOptionalSpec<C, PR> partition(CriteriaContext criteriaContext
            , Function<List<String>, PR> function) {
        return new PartitionClause<>(criteriaContext, function);
    }

    static <C, AR> Statement._LeftParenStringQuadraOptionalSpec<C, Statement._AsClause<AR>> partitionAs(CriteriaContext criteriaContext
            , BiFunction<List<String>, String, AR> function) {
        return new PartitionAsClause<>(criteriaContext, function);
    }


    private final CriteriaContext criteriaContext;

    private List<String> partitionList;

    private boolean optionalPartition;


    private MySQLPartitionClause(CriteriaContext criteriaContext) {
        this.criteriaContext = criteriaContext;
    }


    @Override
    public final Statement._RightParenClause<PR> leftParen(String string) {
        this.optionalPartition = false;
        this.comma(string);
        return this;
    }

    @Override
    public final Statement._CommaStringDualSpec<PR> leftParen(String string1, String string2) {
        this.optionalPartition = false;
        this.comma(string1);
        this.comma(string2);
        return this;
    }

    @Override
    public final Statement._CommaStringQuadraSpec<PR> leftParen(String string1, String string2, String string3, String string4) {
        this.optionalPartition = false;
        this.comma(string1);
        this.comma(string2);
        this.comma(string3);
        this.comma(string4);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> leftParen(Consumer<Consumer<String>> consumer) {
        this.optionalPartition = false;
        consumer.accept(this::comma);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> leftParen(BiConsumer<C, Consumer<String>> consumer) {
        this.optionalPartition = false;
        consumer.accept(this.criteriaContext.criteria(), this::comma);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> leftParenIf(Consumer<Consumer<String>> consumer) {
        this.optionalPartition = true;
        consumer.accept(this::comma);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
        this.optionalPartition = true;
        consumer.accept(this.criteriaContext.criteria(), this::comma);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> comma(String string) {
        List<String> partitionList = this.partitionList;
        if (partitionList == null) {
            partitionList = new ArrayList<>();
            this.partitionList = partitionList;
        } else if (!(partitionList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        partitionList.add(string);
        return this;
    }


    @Override
    public final Statement._CommaStringDualSpec<PR> comma(String string1, String string2) {
        this.comma(string1);
        this.comma(string2);
        return this;
    }

    @Override
    public final Statement._RightParenClause<PR> comma(String string1, String string2, String string3) {
        this.comma(string1);
        this.comma(string2);
        this.comma(string3);
        return this;
    }

    @Override
    public final Statement._CommaStringQuadraSpec<PR> comma(String string1, String string2, String string3, String string4) {
        this.comma(string1);
        this.comma(string2);
        this.comma(string3);
        this.comma(string4);
        return this;
    }

    @Override
    public final PR rightParen() {
        List<String> partitionList = this.partitionList;
        if (partitionList instanceof ArrayList) {
            partitionList = _CollectionUtils.unmodifiableList(partitionList);
            this.partitionList = partitionList;
        } else if (partitionList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        } else if (this.optionalPartition) {
            partitionList = Collections.emptyList();
            this.partitionList = partitionList;
        } else {
            String m = "You use non-leftParenIf clause but don't add partition.";
            throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
        }
        return this.partitionEnd(partitionList);
    }


    abstract PR partitionEnd(List<String> partitionList);


    private static final class PartitionClause<C, PR> extends MySQLPartitionClause<C, PR> {

        private final Function<List<String>, PR> function;

        private PartitionClause(CriteriaContext criteriaContext, Function<List<String>, PR> function) {
            super(criteriaContext);
            this.function = function;
        }


        @Override
        PR partitionEnd(List<String> partitionList) {
            return this.function.apply(partitionList);
        }


    }//PartitionClause

    private static final class PartitionAsClause<C, AR> extends MySQLPartitionClause<C, Statement._AsClause<AR>>
            implements Statement._AsClause<AR> {

        private final BiFunction<List<String>, String, AR> function;


        private PartitionAsClause(CriteriaContext criteriaContext, BiFunction<List<String>, String, AR> function) {
            super(criteriaContext);
            this.function = function;
        }

        @Override
        public AR as(final String alias) {
            if (!_StringUtils.hasText(alias)) {
                String m = "alias of tale must be non-empty.";
                throw CriteriaContextStack.criteriaError(((MySQLPartitionClause<?, ?>) this).criteriaContext, m);
            }
            return this.function.apply(((MySQLPartitionClause<?, ?>) this).partitionList, alias);
        }

        @Override
        Statement._AsClause<AR> partitionEnd(List<String> partitionList) {
            return this;
        }

    }//PartitionAsClause


}
