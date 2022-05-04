package io.army.criteria.impl;

import io.army.criteria.TableItem;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._TableBlock;
import io.army.meta.TableMeta;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class of all single table update statement.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleUpdate<C, SR, WR, WA>
        extends JoinableUpdate<C, SR, Void, Void, Void, Void, Void, Void, Void, WR, WA>
        implements _SingleUpdate, Update._UpdateSpec, Update {

    SingleUpdate(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    @Override
    final _TableBlock createNoOnTableBlock(_JoinType joinType, TableItem table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createTableBlock(_JoinType joinType, TableMeta<?> table, String tableAlias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createItemBlock(_JoinType joinType, TableItem tableItem, String alias) {
        throw _Exceptions.castCriteriaApi();
    }

    @Override
    final Void createNextNoOnClause(_JoinType joinType, TableMeta<?> table) {
        throw _Exceptions.castCriteriaApi();
    }


}
