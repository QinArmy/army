package io.army.meta;

import io.army.criteria.FieldSelection;
import io.army.criteria.LogicalField;
import io.army.criteria.SpecialExpression;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingMeta;

import java.sql.JDBCType;

/**
 * @param <T> Domain Java Type
 * @param <F> Domain property Java Type
 * @see FieldMeta
 * @see LogicalField
 */
public interface FieldExpression<T extends IDomain, F> extends SpecialExpression<F>, FieldSelection {


    TableMeta<T> tableMeta();


    JDBCType jdbcType();

    /**
     * @return 字段对应的 java 类型
     */
    Class<F> javaType();


    MappingMeta mappingMeta();

    String propertyName();

    @Override
    boolean equals(Object o);

    @Override
    int hashCode();
}
