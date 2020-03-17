package io.army.criteria;

/**
 *
 */
public interface Selection extends SelfDescribed, MappingTypeAble{

    String alias();

    @Override
    String toString();

}
