package io.army.criteria;

import io.army.dialect.DqlDialect;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.stmt.ParamValue;

public interface SqlContext {

    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta);

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendFieldPredicate(FieldPredicate predicate);

    void appendText(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendConstant(ParamMeta paramMeta, Object value);

    DqlDialect dql();

    SQLBuilder sqlBuilder();

    void appendParam(ParamValue paramValue);

}
