package io.army.dialect;

import io.army.criteria.FieldPredicate;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.ParamWrapper;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractSQLContext implements TableContextSQLContext {

    protected final Dialect dialect;

    protected final Visible visible;

    protected final SQLBuilder sqlBuilder;

    protected final List<ParamWrapper> paramList;

    protected final TableContextSQLContext parentContext;

    protected AbstractSQLContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = DialectUtils.createSQLBuilder();
        this.paramList = new ArrayList<>();
        this.parentContext = null;
    }

    protected AbstractSQLContext(TableContextSQLContext parentContext) {
        this.dialect = parentContext.dialect();
        this.visible = parentContext.visible();
        this.sqlBuilder = parentContext.sqlBuilder();
        this.paramList = parentContext.paramList();
        this.parentContext = parentContext;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        doAppendField(tableAlias, fieldMeta);
    }

    @Override
    public void appendFieldPredicate(FieldPredicate predicate) {
        predicate.appendPredicate(this);
    }


    @Override
    public final Visible visible() {
        return this.visible;
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public void appendText(String textValue) {
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(textValue));
    }

    @Override
    public final void appendConstant(ParamMeta paramMeta, Object value) {
        MappingMeta mappingMeta = paramMeta.mappingMeta();
        if (mappingMeta instanceof FieldMeta) {
            this.sqlBuilder
                    .append(" ")
                    .append(mappingMeta.toConstant((FieldMeta<?, ?>) mappingMeta, value))
            ;
        } else {
            this.sqlBuilder
                    .append(" ")
                    .append(mappingMeta.toConstant(null, value))
            ;
        }
    }


    @Override
    public final DQL dql() {
        return this.dialect;
    }

    @Override
    public final SQLBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final void appendParam(ParamWrapper paramWrapper) {
        this.paramList.add(paramWrapper);
    }

    @Override
    public final List<ParamWrapper> paramList() {
        return this.paramList;
    }



    @Override
    public final TableContextSQLContext parentContext() {
        return this.parentContext;
    }

    @Nullable
    @Override
    public final   TableContext parentTableContext() {
        return this.parentContext == null ? null :  this.parentContext.tableContext();
    }

    /*################################## blow protected final method ##################################*/

    protected SQLBuilder obtainTablePartBuilder() {
        return this.sqlBuilder;
    }

    protected final void doAppendField(@Nullable String tableAlias, FieldMeta<?, ?> fieldMeta) {
        SQLBuilder builder = obtainTablePartBuilder();
        Dialect dialect = this.dialect;
        builder.append(" ");
        if (tableAlias != null) {
            builder.append(dialect.quoteIfNeed(tableAlias))
                    .append(".");
        }
        builder.append(dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    /*################################## blow protected template method ##################################*/
}
