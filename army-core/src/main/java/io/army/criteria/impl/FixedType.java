package io.army.criteria.impl;

import io.army.criteria.TypeInfer;

/**
 * <p>
 * Package interface. This is base interface of below:
 *     <ul>
 *         <li>{@link TableFieldMeta}</li>
 *         <li>{@link QualifiedFieldImpl}</li>
 *         <li>{@link OperationExpression.PredicateExpression}</li>
 *         <li>{@link SingleParamExpression}</li>
 *         <li>{@link SingleLiteralExpression}</li>
 *         <li>{@link SQLs#NULL}</li>
 *         <li>{@link NonOperationExpression.MultiValueExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface FixedType extends TypeInfer {


}
