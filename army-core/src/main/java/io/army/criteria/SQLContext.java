package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.List;

public interface SQLContext {

    SQLStatement sqlStatement();


    void appendField(String tableAlias, FieldMeta<?,?> fieldMeta)throws TableAliasException;

    void quoteIfKeyAndAppend(String textValue);

    /**
     * @see ConstantExpression
     */
    void appendTextValue(MappingType mappingType,Object value);

    SQL sql();

    StringBuilder stringBuilder();

    void appendParam(ParamWrapper paramWrapper);

    List<ParamWrapper> paramWrapper();


}
