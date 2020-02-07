package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.SQLContext;
import io.army.criteria.TableAliasException;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.modelgen.MetaConstant;

import java.util.List;
import java.util.Map;

final class SingleUpdateSQLContext implements SQLContext {

    private final SQL sql;

    private final StringBuilder builder;

    private final List<ParamWrapper> paramWrapperList;

    private final String tableAlias;

    private final TableMeta<?> tableMeta;

    public SingleUpdateSQLContext(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList
            , String tableAlias, TableMeta<?> tableMeta) {
        this.sql = sql;
        this.builder = builder;
        this.paramWrapperList = paramWrapperList;
        this.tableAlias = tableAlias;
        this.tableMeta = tableMeta;
    }

    @Override
    public void registerAlias(String alias, TableMeta<?> tableMeta) throws TableAliasException {
        if (this.tableAlias.equals(alias) && tableMeta != this.tableMeta) {
            throw new TableAliasException(ErrorCode.CRITERIA_ERROR
                    , "alias[%s] only %s%s ,not %s%s"
                    , this.tableAlias
                    , this.tableMeta.javaType().getSimpleName()
                    , MetaConstant.META_CLASS_NAME_SUFFIX
                    , tableMeta.javaType().getSimpleName()
                    , MetaConstant.META_CLASS_NAME_SUFFIX);
        }
    }

    @Override
    public void quoteIfKeyAndAppend(String textValue) {
        builder.append(sql.quoteIfNeed(textValue));
    }

    @Override
    public void appendTextValue(MappingType mappingType, Object value) {
        builder.append(
                DialectUtils.quoteIfNeed(mappingType, mappingType.nonNullTextValue(value))
        );
    }

    @Override
    public SQL sql() {
        return sql;
    }

    @Override
    public StringBuilder stringBuilder() {
        return builder;
    }

    @Override
    public void appendParam(ParamWrapper paramWrapper) {
        paramWrapperList.add(paramWrapper);
    }

    @Override
    public List<ParamWrapper> paramWrapper() {
        return paramWrapperList;
    }
}
