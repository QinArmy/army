package io.army.criteria.impl;

import io.army.criteria.Update;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class of all single table update statement.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleUpdate<C, SR, WR, WA>
        extends JoinableUpdate<C, SR, Void, Void, Void, Void, Void, Void, WR, WA>
        implements _SingleUpdate, Update._UpdateSpec, Update {


    SingleUpdate(CriteriaContext criteriaContext) {
        super(JoinableClause.voidClauseSuppler(), criteriaContext);
    }


    @Override
    final void crossJoinEvent(boolean success) {
        throw _Exceptions.castCriteriaApi();
    }


}
