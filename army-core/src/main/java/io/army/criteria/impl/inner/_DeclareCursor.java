package io.army.criteria.impl.inner;

import io.army.criteria.SubQuery;

public interface _DeclareCursor extends _Statement {

    String cursorName();

    SubQuery forQuery();

}
