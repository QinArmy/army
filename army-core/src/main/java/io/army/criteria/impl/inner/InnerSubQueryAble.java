package io.army.criteria.impl.inner;

import io.army.criteria.QueryAble;

@DeveloperForbid
public interface InnerSubQueryAble extends InnerBasicQueryAble {

    QueryAble outerQuery();

}
