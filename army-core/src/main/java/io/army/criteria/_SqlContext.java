package io.army.criteria;

import io.army.dialect.Dialect;
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
