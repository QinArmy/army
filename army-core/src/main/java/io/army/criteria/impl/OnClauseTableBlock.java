package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.lang.Nullable;
import io.army.util.ArrayUtils;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

class OnClauseTableBlock<C, OR> extends TableBlock implements Statement._OnClause<C, OR> {

    private List<_Predicate> predicateList;

    private final OR stmt;

    OnClauseTableBlock(_JoinType joinType, TableItem tableItem, String alias, OR stmt) {
        super(joinType, tableItem, alias);
        this.stmt = stmt;
    }

    OnClauseTableBlock(BlockParams params, OR stmt) {
        super(params);
        this.stmt = stmt;
    }


    @Override
    public final OR on(IPredicate predicate) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
        }
        this.predicateList = Collections.singletonList((_Predicate) predicate);
        return this.stmt;
    }

    @Override
    public final OR on(IPredicate predicate1, IPredicate predicate2) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(CriteriaUtils.getCriteriaContext(this.stmt));
        }
        this.predicateList = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) predicate1,
                (OperationPredicate) predicate2
        );
        return this.stmt;
    }

    @Override
    public final OR on(Function<Object, IPredicate> operator, DataField operandField) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        this.predicateList = Collections.singletonList((OperationPredicate) operator.apply(operandField));
        return this.stmt;
    }

    @Override
    public final OR on(Function<Object, IPredicate> operator, Supplier<?> operandSupplier) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        final OperationPredicate predicate;
        predicate = (OperationPredicate) operator.apply(operandSupplier.get());
        this.predicateList = Collections.singletonList(predicate);
        return this.stmt;
    }

    @Override
    public final OR on(Function<Object, IPredicate> operator, Function<String, Object> function, String keyName) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        final OperationPredicate predicate;
        predicate = (OperationPredicate) operator.apply(function.apply(keyName));
        this.predicateList = Collections.singletonList(predicate);
        return this.stmt;
    }

    @Override
    public final OR on(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstSupplier, Supplier<?> secondSupplier) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        final OperationPredicate predicate;
        predicate = (OperationPredicate) operator.apply(firstSupplier.get(), secondSupplier.get());
        this.predicateList = Collections.singletonList(predicate);
        return this.stmt;
    }

    @Override
    public final OR on(BiFunction<Object, Object, IPredicate> operator, Function<String, Object> function, String firstKey, String secondKey) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        final OperationPredicate predicate;
        predicate = (OperationPredicate) operator.apply(function.apply(firstKey), function.apply(secondKey));
        this.predicateList = Collections.singletonList(predicate);
        return this.stmt;
    }

    @Override
    public final OR on(Function<Object, IPredicate> operator1, DataField operandField1
            , Function<Object, IPredicate> operator2, DataField operandField2) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        this.predicateList = ArrayUtils.asUnmodifiableList(
                (OperationPredicate) operator1.apply(operandField1),
                (OperationPredicate) operator2.apply(operandField2)
        );
        return this.stmt;
    }

    @Override
    public final OR on(Consumer<Consumer<IPredicate>> consumer) {
        if (this.predicateList != null) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        consumer.accept(this::addPredicate);
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            throw predicateListIsEmpty();
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
        return this.stmt;
    }

    @Override
    public final OR on(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.getCriteria(), this::addPredicate);
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            throw predicateListIsEmpty();
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicateList);
        return this.stmt;
    }

    @Override
    public final List<_Predicate> predicateList() {
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null | predicateList instanceof ArrayList) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        return predicateList;
    }


    @Nullable
    @SuppressWarnings("unchecked")
    final C getCriteria() {
        return ((CriteriaSpec<C>) this.stmt).getCriteria();
    }

    final CriteriaContext getCriteriaContext() {
        return ((CriteriaContextSpec) this.stmt).getCriteriaContext();
    }


    private void addPredicate(final IPredicate predicate) {
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.getCriteriaContext());
        }
        predicateList.add((OperationPredicate) predicate);
    }

    private CriteriaException predicateListIsEmpty() {
        return CriteriaContextStack.criteriaError(this.getCriteriaContext()
                , _Exceptions::predicateListIsEmpty);
    }


}
