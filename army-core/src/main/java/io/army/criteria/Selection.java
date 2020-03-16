package io.army.criteria;

/**
 * 代表 prepareSelect 列表中的元素
 * created  on 2018/10/8.
 */
public interface Selection extends SelfDescribed, MappingTypeAble{

    String alias();

    @Override
    String toString();

}
