package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.List;

public interface SQLContext {

    void registerAlias(String alias, TableMeta<?> tableMeta)throws TableAliasException;

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
