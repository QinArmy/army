package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TableAble;
import io.army.lang.NonNull;

import java.util.List;

@DeveloperForbid
public interface TableWrapper {

    @NonNull
    TableAble tableAble();

    String alias();

    SQLModifier jointType();

    List<IPredicate> onPredicateList();
}
