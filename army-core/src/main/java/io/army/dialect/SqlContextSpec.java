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
*
 * @since 1.0
 */
interface SqlContextSpec {


    /**
     * <p>
     * This method is designed for the implementation of {@link QualifiedField}
     *
     * <p>
     *     <ol>
     *         <li>append one space</li>
     *         <li>append table alias and point</li>
     *         <li>append safe column name</li>
     *     </ol>
     *
     */
    void appendField(String tableAlias, FieldMeta<?> field);

    /**
     * <p>
     * This method is designed for the implementation of {@link FieldMeta}
     *
     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append table alias and point if need</li>
     *         <li>append safe column name</li>
     *     </ol>
     *
     */
    void appendField(FieldMeta<?> field);


    /**
     * <p>
     * just append column name, no preceding space ,no preceding table alias
     *
     * <p>
     * This method is designed for postgre EXCLUDED in INSERT statement.
     *
     */
    void appendFieldOnly(FieldMeta<?> field);

}
