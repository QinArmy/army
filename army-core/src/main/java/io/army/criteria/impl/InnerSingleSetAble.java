package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.criteria.SingleSetAble;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerSingleSetAble<T extends IDomain> extends SingleSetAble<T> {

    TableMeta<T> tableMeta();

    List<FieldMeta<T, ?>> targetFieldList();

    List<Expression<?>> valueExpressionList();

    List<Predicate> predicateList();

    @Nullable
    FieldMeta<T, ?> orderField();

    int rowCount();

}
