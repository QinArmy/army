package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.QualifiedField;


/**
 * package interface
 *
 * @since 1.0
 */
interface ItemField<T, I extends Item> extends QualifiedField<T>, _ItemExpression<I> {


}
