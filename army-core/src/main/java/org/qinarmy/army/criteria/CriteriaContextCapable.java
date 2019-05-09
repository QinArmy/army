package org.qinarmy.army.criteria;

import org.springframework.lang.NonNull;

/**
 * created  on 2019-01-30.
 */
public interface CriteriaContextCapable {

    @NonNull
    CriteriaContext getCriteriaContext();
}
