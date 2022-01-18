package io.army.criteria;

/**
 *
 */
public interface Selection extends SelectPart, TypeInfer, SortPart {

    String alias();

    boolean nullable();


}
