package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

@DeveloperForbid
public interface InnerStandardInsert extends InnerInsert {


    /**
     * @return a unmodifiable list
     * @see Insert.InsertOptionAble#commonValue(FieldMeta, Expression)
     */
    Map<FieldMeta<?, ?>, Expression<?>> commonValueMap();

    /**
     * @see Insert.InsertOptionAble#alwaysUseCommonValue()
     */
    boolean alwaysUseCommonExp();

    /**
     * @see Insert.InsertOptionAble#defaultIfNull()
     */
    boolean defaultExpIfNull();


    /**
     * @see Insert.InsertValuesAble
     * @see Insert.BatchInsertIntoAble#insert(IDomain)
     */
    List<IDomain> valueList();
}
