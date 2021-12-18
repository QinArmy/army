package io.army.criteria;

/**
 *
 */
public interface Selection extends SelectPart, MappingMetaAble, SortPart {

    String alias();

    boolean nullable();

    @Override
    String toString();

}
