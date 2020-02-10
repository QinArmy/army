package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.criteria.SQLStatement;
import io.army.criteria.TableAliasException;
import io.army.dialect.DialectUtils;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;

import java.util.ArrayList;
import java.util.List;

class DefaultSQLContext implements SQLContext {


    final SQL sql;

    final SQLStatement sqlStatement;

    final StringBuilder builder = new StringBuilder();

    final List<ParamWrapper> paramWrapperList = new ArrayList<>();

    DefaultSQLContext(SQL sql, SQLStatement sqlStatement) {
        this.sql = sql;
        this.sqlStatement = sqlStatement;
    }

    @Override
    public SQLStatement sqlStatement() {
        return sqlStatement;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        builder.append(this.sql.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.sql.quoteIfNeed(fieldMeta.fieldName()));
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

    protected final void throwTableAliasError(String tableAlias,FieldMeta<?,?> fieldMeta){
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR,"table alias[%s] field[%s] error"
                ,tableAlias
                ,fieldMeta.propertyName());
    }
}
