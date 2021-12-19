package io.army.criteria;

/**
 *
 */
public interface Selection extends SelectPart, MappingTypeAble, SortPart {

    String alias();

    boolean nullable();

    @Override
    String toString();

}
