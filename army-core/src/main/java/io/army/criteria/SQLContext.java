package io.army.criteria;

import io.army.dialect.DQL;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.ParamWrapper;

public interface SQLContext {


    void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta);

    void appendField(FieldMeta<?, ?> fieldMeta);

    void appendFieldPredicate(SpecialPredicate predicate);

    void appendText(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendTextValue(MappingMeta mappingType, Object value);

    DQL dql();

    StringBuilder sqlBuilder();

    void appendParam(ParamWrapper paramWrapper);

}
