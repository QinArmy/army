package io.army.dialect;

import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractSQLContext implements _TablesSqlContext {

    protected final Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected final _TablesSqlContext parentContext;

    protected AbstractSQLContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();
        this.parentContext = null;
    }

    protected AbstractSQLContext(_TablesSqlContext parentContext) {
        this.dialect = parentContext.dialect();
        this.visible = parentContext.visible();
        this.sqlBuilder = parentContext.sqlBuilder();
        this.paramList = parentContext.paramList();
        this.parentContext = parentContext;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> field) {
        doAppendField(tableAlias, field);
    }


    @Override
    public final Visible visible() {
        return this.visible;
    }



    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }

    @Override
    public final void appendParam(ParamValue paramValue) {
        this.paramList.add(paramValue);
    }

    @Override
    public final List<ParamValue> paramList() {
        return this.paramList;
    }


    @Override
    public final _TablesSqlContext parentContext() {
        return this.parentContext;
    }

    @Nullable
    @Override
    public final TablesContext parentTableContext() {
        return this.parentContext == null ? null : this.parentContext.tableContext();
    }

    /*################################## blow protected final method ##################################*/

    protected StringBuilder obtainTablePartBuilder() {
        return this.sqlBuilder;
    }

    protected final void doAppendField(@Nullable String tableAlias, FieldMeta<?, ?> fieldMeta) {
        StringBuilder builder = obtainTablePartBuilder();
        Dialect dialect = this.dialect;
        builder.append(" ");
        if (tableAlias != null) {
            builder.append(dialect.quoteIfNeed(tableAlias))
                    .append(".");
        }
        builder.append(dialect.quoteIfNeed(fieldMeta.columnName()));
    }

    /*################################## blow protected template method ##################################*/
}
