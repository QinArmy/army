package io.army.dialect;


import io.army.criteria.Selection;

import java.util.List;

/**
 * package interface
 */
interface MyBatchSpecContext extends _SqlContext {

    List<Selection> selectionList();

    boolean hasOptimistic();

    int nextGroup();

    int groupSize();
}
