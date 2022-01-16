package io.army.dialect;

import io.army.criteria.QualifiedField;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.stmt.ParamValue;

public interface _SqlContext {


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
    void appendField(String tableAlias, FieldMeta<?, ?> field);

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
    void appendField(FieldMeta<?, ?> field);

    _Dialect dialect();

    StringBuilder sqlBuilder();

    /**
     * <p>
     * This method is designed for parameter expression.
     * </p>
     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append '?' to {@link #sqlBuilder()}</li>
     *         <li>append paramValue to param list</li>
     *     </ol>
     * </p>
     */
    void appendParam(ParamValue paramValue);

    Visible visible();

}
