package io.army.dialect;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;
import io.army.criteria.SQLContext;
import io.army.criteria.TableAliasException;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSQLContext implements SQLContext {

    protected final DML dml;

    protected final DQL dql;

    protected final StringBuilder builder = new StringBuilder();

    protected final List<ParamWrapper> paramList = new ArrayList<>();

    boolean finished;

    AbstractSQLContext(DML dml, DQL dql) {
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
    public final StringBuilder sqlBuilder() {
        return builder;
    }

    @Override
    public final void appendParam(ParamWrapper paramWrapper) {
        paramList.add(paramWrapper);
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return paramList;
    }

    @Override
    public SQLWrapper build() {
        Assert.state(!this.finished, "SQLContext finished.");

        this.finished = true;
        return SQLWrapper.build(
                builder.toString()
                , paramList
        );
    }

    protected final void throwTableAliasError(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        throw new CriteriaException(ErrorCode.CRITERIA_ERROR, "field alias[%s] field[%s] error"
                , tableAlias
                , fieldMeta.propertyName());
    }
}
