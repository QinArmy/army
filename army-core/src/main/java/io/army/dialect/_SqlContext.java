package io.army.dialect;

import io.army.criteria.ConstantExpression;
import io.army.criteria.FieldPredicate;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

public interface _SqlContext {

    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta);

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendFieldPredicate(FieldPredicate predicate);

    void appendIdentifier(String identifier);

    /**
     * @see ConstantExpression
     */
    void appendConstant(ParamMeta paramMeta, Object value);

    Dialect dialect();

    StringBuilder sqlBuilder();

    void appendParam(ParamValue paramValue);

}
