package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.criteria.SetAbleOfSingleUpdate;
import io.army.criteria.SingleUpdateAble;
import io.army.criteria.impl.DeveloperForbid;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerSingleUpdateAble extends SingleUpdateAble {

    String tableAlias();

    TableMeta<?> tableMeta();

    List<FieldMeta<?, ?>> targetFieldList();

    List<Expression<?>> valueExpressionList();

    List<Predicate> predicateList();

   List< Expression<?>> orderExpList();

    List<Boolean> ascExpList();

    int rowCount();

}
