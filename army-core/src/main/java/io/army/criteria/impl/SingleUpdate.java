package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

abstract class SingleUpdate<C, WR, WA, SR> extends JoinableUpdate<C, Void, Void, WR, WA, SR>
        implements _SingleUpdate {

    SingleUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }


    @Override
    final Void createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createOnBlock(_JoinType joinType, TableItem tableItem, String alias) {
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
