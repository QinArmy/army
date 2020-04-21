package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.criteria.TableAliasException;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

import java.util.ArrayList;
import java.util.List;

class DefaultSQLContext implements SQLContext {


    final DML dml;

    final DQL dql;

    final StringBuilder builder = new StringBuilder();

    final List<ParamWrapper> paramWrapperList = new ArrayList<>();

    DefaultSQLContext(DML dml, DQL dql) {
        this.dml = dml;
        this.dql = dql;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        builder.append(this.dml.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.dml.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public final void appendText(String textValue) {
        builder.append(" ")
                .append(dml.quoteIfNeed(textValue));
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
    public final DML dml() {
        return dml;
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
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR,"field alias[%s] field[%s] error"
                ,tableAlias
                ,fieldMeta.propertyName());
    }
}
