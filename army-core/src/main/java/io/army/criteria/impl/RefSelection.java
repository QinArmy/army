package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;

interface RefSelection<E> extends Expression<E>, Selection {

    String subQueryAlias();

    String derivedFieldName();

    Selection selection();

    void selection(Selection selection);


}
