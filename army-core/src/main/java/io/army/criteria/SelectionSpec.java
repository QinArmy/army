package io.army.criteria;


import io.army.meta.ParamMeta;

/**
 * <p>
 * This interface representing the ability that create {@link Selection}.
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Expression}</li>
 *         <li>{@link VarExpression}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SelectionSpec {

    /**
     * @param alias non-null,non-empty.
     */
    Selection as(String alias);


    SelectionSpec asType(ParamMeta paramMeta);

}
