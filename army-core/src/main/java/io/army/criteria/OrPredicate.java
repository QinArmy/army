package io.army.criteria;

import java.util.List;

public interface OrPredicate extends IPredicate {

    IPredicate leftPredicate();

    List<IPredicate> rightPredicate();
}
