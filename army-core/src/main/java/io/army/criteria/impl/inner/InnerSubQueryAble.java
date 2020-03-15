package io.army.criteria.impl.inner;

import io.army.criteria.Select;

@DeveloperForbid
public interface InnerSubQueryAble extends InnerQueryAble {

    Select outerQuery();

}
