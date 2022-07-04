package io.army.criteria.impl;

import io.army.criteria.Statement;
import io.army.criteria.mysql.MySQLQuery;
import io.army.util._CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

final class MySQLPartitionClause<C, PR> implements MySQLQuery._PartitionLeftParenClause<C, PR>
        , MySQLQuery._PartitionCommaDualClause<PR>, MySQLQuery._PartitionCommaQuadraClause<PR> {


    private final CriteriaContext criteriaContext;

    private final Function<List<String>, PR> function;

    private List<String> partitionList;

    private boolean optionalPartition;

    MySQLPartitionClause(CriteriaContext criteriaContext, Function<List<String>, PR> function) {
        this.criteriaContext = criteriaContext;
        this.function = function;
    }


    @Override
    public Statement._RightParenClause<PR> leftParen(String partitionName) {
        this.optionalPartition = false;
        this.comma(partitionName);
        return this;
    }

    @Override
    public MySQLQuery._PartitionCommaDualClause<PR> leftParen(String partitionName1, String partitionName2) {
        this.optionalPartition = false;
        this.comma(partitionName1);
        this.comma(partitionName2);
        return this;
    }

    @Override
    public MySQLQuery._PartitionCommaQuadraClause<PR> leftParen(String partitionName1, String partitionName2, String partitionName3, String partitionName4) {
        this.optionalPartition = false;
        this.comma(partitionName1);
        this.comma(partitionName2);
        this.comma(partitionName3);
        this.comma(partitionName4);
        return this;
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
    public Statement._RightParenClause<PR> comma(String partitionName) {
        List<String> partitionList = this.partitionList;
        if (partitionList == null) {
            partitionList = new ArrayList<>();
            this.partitionList = partitionList;
        } else if (!(partitionList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.criteriaContext);
        }
        partitionList.add(partitionName);
        return this;
    }


    @Override
    public MySQLQuery._PartitionCommaDualClause<PR> comma(String partitionName1, String partitionName2) {
        this.comma(partitionName1);
        this.comma(partitionName2);
        return this;
    }

    @Override
    public Statement._RightParenClause<PR> comma(String partitionName1, String partitionName2, String partitionName3) {
        this.comma(partitionName1);
        this.comma(partitionName2);
        this.comma(partitionName3);
        return this;
    }

    @Override
    public MySQLQuery._PartitionCommaQuadraClause<PR> comma(String partitionName1, String partitionName2, String partitionName3, String partitionName4) {
        this.comma(partitionName1);
        this.comma(partitionName2);
        this.comma(partitionName3);
        this.comma(partitionName4);
        return this;
    }

    @Override
    public PR rightParen() {
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
        return this.function.apply(partitionList);
    }


}
