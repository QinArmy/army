package io.army.criteria;

import io.army.dialect.DQL;
import io.army.dialect.SQLBuilder;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.wrapper.ParamWrapper;

public interface SQLContext {

    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta);

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendFieldPredicate(FieldPredicate predicate);

    void appendText(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendConstant(ParamMeta paramMeta, Object value);

    DQL dql();

    SQLBuilder sqlBuilder();

    void appendParam(ParamWrapper paramWrapper);

}
