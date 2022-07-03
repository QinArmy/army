package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.DmlStatement;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.util._Exceptions;

/**
 * <p>
 * This class is base class of all single table update statement.
 * </p>
 *
 * @since 1.0
 */
abstract class SingleUpdate<C, F extends DataField, SR, WR, WA, U extends DmlStatement.DmlUpdate>
        extends JoinableUpdate<C, F, SR, Void, Void, Void, Void, Void, Void, WR, WA, U>
        implements _SingleUpdate {


    SingleUpdate(CriteriaContext criteriaContext) {
        super(JoinableClause.voidClauseSuppler(), criteriaContext);
    }


    @Override
    final void crossJoinEvent(boolean success) {
        throw _Exceptions.castCriteriaApi();
    }


}
