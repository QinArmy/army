package io.army.dialect;

import io.army.criteria.ConstantExpression;
import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

public interface _SqlContext {

    /**
     * <p>
     * This method is used by non {@link io.army.criteria.SetTargetPart}.
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
     * This method is used by non {@link io.army.criteria.SetTargetPart}.
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

    @Deprecated
    default void appendFieldPredicate(FieldPredicate predicate) {
        throw new UnsupportedOperationException();
    }


    void appendIdentifier(String identifier);

    /**
     * @see ConstantExpression
     */
    void appendConstant(ParamMeta paramMeta, Object value);

    Dialect dialect();

    StringBuilder sqlBuilder();

    /**
     * <p>
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
