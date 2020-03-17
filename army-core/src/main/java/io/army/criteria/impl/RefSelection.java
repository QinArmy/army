package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.lang.Nullable;

interface RefSelection<E> extends Expression<E> {

     String subQueryAlias();

     String derivedFieldName();

     Selection selection();

     void selection(Selection selection);

}
