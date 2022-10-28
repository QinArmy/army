package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.DmlStatement;
import io.army.criteria.impl.inner._SingleUpdate;

/**
 * <p>
 * This class is base class of all single table update statement.
 * </p>
 *
 * @since 1.0
 */
@Deprecated
abstract class SingleUpdate0<C, F extends DataField, SR, WR, WA, U extends DmlStatement.DmlUpdate>
        extends JoinableUpdate0<C, F, SR, Void, Void, Void, Void, Void, Void, Void, WR, WA, U>
        implements _SingleUpdate {


    SingleUpdate0(CriteriaContext criteriaContext) {
        super(criteriaContext, JoinableClause.voidClauseCreator());

    }


}
