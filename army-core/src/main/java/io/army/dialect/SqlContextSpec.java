package io.army.dialect;

import io.army.criteria.QualifiedField;
import io.army.meta.FieldMeta;

/**
 * <p>
 * This interface is base interface of following:
 *     <ul>
 *         <li>{@link _SqlContext}</li>
 *         <li>{@link _MultiTableContext}</li>
 *     </ul>
 *     declare common method.
 * </p>
 *
 * @since 1.0
 */
interface SqlContextSpec {


    /**
     * <p>
     * This method is designed for the implementation of {@link QualifiedField}
     * </p>
     * <p>
     *     <ol>
     *         <li>append one space</li>
     *         <li>append table alias and point</li>
     *         <li>append safe column name</li>
     *     </ol>
     * </p>
     */
    void appendField(String tableAlias, FieldMeta<?> field);

    /**
     * <p>
     * This method is designed for the implementation of {@link FieldMeta}
     * </p>
     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append table alias and point if need</li>
     *         <li>append safe column name</li>
     *     </ol>
     * </p>
     */
    void appendField(FieldMeta<?> field);


    /**
     * <p>
     * just append column name, no preceding space ,no preceding table alias
     * </p>
     * <p>
     * This method is designed for postgre EXCLUDED in INSERT statement.
     * </p>
     */
    void appendFieldOnly(FieldMeta<?> field);

}
