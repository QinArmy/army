package io.army.dialect;

import io.army.criteria.NamedLiteral;
import io.army.criteria.QualifiedField;
import io.army.criteria.SQLParam;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.TypeMeta;

/**
 * <p>
 * This interface representing sql context,that is used by {@link  DialectParser} and the implementation of criteria api,
 * for example {@link  io.army.criteria.impl.inner._Expression}.
 * </p>
 *
 * @since 1.0
 */
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

    DialectParser parser();

    StringBuilder sqlBuilder();

    /**
     * <p>
     * This method is designed for parameter expression.
     * </p>
     * <p> steps:
     *     <ol>
     *         <li>append one space</li>
     *         <li>append '?' to {@link #sqlBuilder()}</li>
     *         <li>append sqlParam to param list</li>
     *     </ol>
     * </p>
     */
    void appendParam(SQLParam sqlParam);

    void appendLiteral(TypeMeta paramMeta, Object nonNull);

    void appendPattern(String pattern);

    void appendLiteral(NamedLiteral namedLiteral);

    Visible visible();


}
