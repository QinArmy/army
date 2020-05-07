package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;

interface RefSelection<E> extends Expression<E> {

    String subQueryAlias();

    String derivedFieldName();

    boolean finished();

    void selection(String subQueryAlias, Selection selection);

}
