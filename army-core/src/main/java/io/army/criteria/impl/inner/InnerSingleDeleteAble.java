package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;

public interface InnerSingleDeleteAble {

    TableMeta<?> tableMeta();

    List<IPredicate> predicateList();

    List<Expression<?>> orderExpList();

    List<Boolean> ascList();

    int rowCount();
}
