package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.UpdateAble;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdateAble extends UpdateAble {

    void prepare();

    String tableAlias();

    TableMeta<?> tableMeta();

    List<FieldMeta<?, ?>> targetFieldList();

    List<Expression<?>> valueExpressionList();

    List<IPredicate> predicateList();

}
