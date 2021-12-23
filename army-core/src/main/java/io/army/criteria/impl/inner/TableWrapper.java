package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;

import java.util.List;

public interface TableWrapper {

    TablePart tableAble();

    String alias();

    SQLModifier jointType();

    List<IPredicate> onPredicateList();

    int databaseIndex();

    int tableIndex();
}
