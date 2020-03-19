package io.army.criteria.impl.inner;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Update;
import io.army.meta.FieldMeta;

import java.util.List;

@DeveloperForbid
public interface InnerUpdate extends Update, InnerSQL {

    List<TableWrapper> tableWrapperList();

    List<FieldMeta<?, ?>> targetFieldList();

    List<Expression<?>> valueExpList();

    List<IPredicate> predicateList();

}
