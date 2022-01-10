package io.army.criteria.impl;

import io.army.criteria.TablePart;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

abstract class SingleUpdate<C, WR, WA, SR> extends AbstractUpdate<C, Void, Void, WR, WA, SR> implements _SingleUpdate {

    SingleUpdate(@Nullable C criteria) {
        super(criteria);
    }


    @Override
    final Void addTableBlock(JoinType joinType, TableMeta<?> table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void addOnBlock(JoinType joinType, TablePart tablePart, String alias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createNoActionTableBlock() {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createNoActionOnBlock() {
        throw _Exceptions.castCriteriaApi();
    }


    @Override
    final Void getNoActionTableBlock() {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void getNoActionOnBlock() {
        throw _Exceptions.castCriteriaApi();
    }


}
