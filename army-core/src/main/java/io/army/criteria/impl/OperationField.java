package io.army.criteria.impl;

import io.army.criteria.GenericField;
import io.army.domain.IDomain;

/**
 * <p>
 * This class is base class of the implementation of below:
 *     <ul>
 *         <li>{@link io.army.meta.FieldMeta}</li>
 *         <li>{@link io.army.criteria.QualifiedField}</li>
 *     </ul>
 * </p>
 */
abstract class OperationField<T extends IDomain, E> extends OperationExpression<E> implements GenericField<T, E> {


}
