package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.mysql.MySQLQuery;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

final class MySQLPartitionClause<C, PR> implements MySQLQuery._PartitionLeftParenClause<C, PR>
        , MySQLQuery._PartitionCommaClause<PR> {


    private final CriteriaContext criteriaContext;

    private final Function<List<String>, PR> function;

    private List<String> partitionList;

    private boolean optionalPartition;

    MySQLPartitionClause(CriteriaContext criteriaContext, Function<List<String>, PR> function) {
        this.criteriaContext = criteriaContext;
        this.function = function;
    }


    @Override
    public MySQLQuery._PartitionCommaClause<PR> leftParen(String partitionName) {
        this.optionalPartition = false;
        return this.comma(partitionName);
    }
    @Override
    public Statement._RightParenClause<PR> leftParen(String partitionName1, String partitionName2) {
        this.optionalPartition = false;
        return this.comma(partitionName1)
                .comma(partitionName2);
    }
    @Override
    public Statement._RightParenClause<PR> leftParen(String partitionName1, String partitionName2, String partitionName3) {
        this.optionalPartition = false;
        return this.comma(partitionName1)
                .comma(partitionName2)
                .comma(partitionName3);
    }
    @Override
    public Statement._RightParenClause<PR> leftParen(Consumer<Consumer<String>> consumer) {
        this.optionalPartition = false;
        consumer.accept(this::comma);
        return this;
    }
    @Override
    public Statement._RightParenClause<PR> leftParen(BiConsumer<C, Consumer<String>> consumer) {
        this.optionalPartition = false;
        consumer.accept(this.criteriaContext.criteria(), this::comma);
        return this;
    }
    @Override
    public Statement._RightParenClause<PR> leftParenIf(Consumer<Consumer<String>> consumer) {
        this.optionalPartition = true;
        consumer.accept(this::comma);
        return this;
    }
    @Override
    public Statement._RightParenClause<PR> leftParenIf(BiConsumer<C, Consumer<String>> consumer) {
        this.optionalPartition = true;
        consumer.accept(this.criteriaContext.criteria(), this::comma);
        return this;
    }

    @Override
    public MySQLQuery._PartitionCommaClause<PR> comma(String partitionName) {
        List<String> partitionList = this.partitionList;
        if (partitionList == null) {
            partitionList = new ArrayList<>();
            this.partitionList = partitionList;
        } else if (!(partitionList instanceof ArrayList)) {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
        }
        partitionList.add(partitionName);
        return this;
    }


    @Override
    public PR rightParen() {
        List<String> partitionList = this.partitionList;
        if (partitionList instanceof ArrayList) {
            partitionList = _CollectionUtils.unmodifiableList(partitionList);
            this.partitionList = partitionList;
        } else if (partitionList != null) {
            throw CriteriaContextStack.criteriaError(this.criteriaContext, _Exceptions::castCriteriaApi);
        } else if (this.optionalPartition) {
            partitionList = Collections.emptyList();
            this.partitionList = partitionList;
        } else {
            String m = "You use non-leftParenIf clause but don't add partition.";
            throw CriteriaContextStack.criteriaError(this.criteriaContext, m);
        }
        return this.function.apply(partitionList);
    }


}
