package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SingleUpdateAble;
import io.army.criteria.impl.DeveloperForbid;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerSingleUpdateAble extends SingleUpdateAble {

    String tableAlias();

    TableMeta<?> tableMeta();

    List<FieldMeta<?, ?>> targetFieldList();

    List<Expression<?>> valueExpressionList();

    List<IPredicate> predicateList();

   List< Expression<?>> orderExpList();

    List<Boolean> ascExpList();

    int rowCount();

}
