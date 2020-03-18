package io.army.criteria;

/**
 *
 */
public interface Selection extends SelectPart, MappingTypeAble {

    String alias();

    @Override
    String toString();

}
