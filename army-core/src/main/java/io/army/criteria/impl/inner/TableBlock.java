package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;

import java.util.List;

public interface TableBlock {

    TablePart table();

    String alias();

    SQLModifier jointType();

    List<IPredicate> onPredicateList();

    byte databaseRoute();

    byte tableRoute();

}
