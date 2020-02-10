package io.army.criteria;

import io.army.meta.mapping.MappingType;

import java.sql.JDBCType;

/**
 * 代表 prepareSelect 列表中的元素
 * created  on 2018/10/8.
 */
public interface Selection {

    String alias();

    Expression<?> expression();

    @Override
    String toString();

}
