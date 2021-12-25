package io.army.criteria.impl.inner;

import io.army.criteria.SQLModifier;
import io.army.criteria.TablePart;

import java.util.List;

public interface _TableBlock {

    TablePart table();

    String alias();

    SQLModifier jointType();

    List<_Predicate> predicates();

}
