package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.TableAliasException;
import io.army.dialect.DialectUtils;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.ArrayList;
import java.util.List;

class DefaultSQLContext implements SQLContext {

    private final SQL sql;

    private final StringBuilder builder = new StringBuilder();

    private final List<ParamWrapper> paramWrapperList = new ArrayList<>();

    DefaultSQLContext( SQL sql) {
        this.sql = sql;
    }

    @Override
    public void registerAlias(String alias, TableMeta<?> tableMeta) throws TableAliasException {

    }

    @Override
    public final void quoteIfKeyAndAppend(String textValue) {
        builder.append(sql.quoteIfNeed(textValue));
    }

    @Override
    public final void appendTextValue(MappingType mappingType, Object value) {
        builder.append(
                DialectUtils.quoteIfNeed(
                        mappingType
                        , mappingType.nonNullTextValue(value)
                )
        );
    }

    @Override
    public final SQL sql() {
        return sql;
    }

    @Override
    public final StringBuilder stringBuilder() {
        return builder;
    }

    @Override
    public final void appendParam(ParamWrapper paramWrapper) {
        paramWrapperList.add(paramWrapper);
    }

    @Override
    public final List<ParamWrapper> paramWrapper() {
        return paramWrapperList;
    }
 }
