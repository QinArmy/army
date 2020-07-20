package io.army.dialect;

import io.army.criteria.SpecialPredicate;
import io.army.criteria.Visible;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractSQLContext implements TableContextSQLContext {

    protected final Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamWrapper> paramList;

    private final TableContextSQLContext parentContext;

    protected AbstractSQLContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder();
        this.paramList = new ArrayList<>();
        this.parentContext = null;
    }

    protected AbstractSQLContext(TableContextSQLContext original) {
        this.dialect = original.dialect();
        this.visible = original.visible();
        this.sqlBuilder = original.sqlBuilder();
        this.paramList = original.paramList();
        this.parentContext = original;
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        doAppendFiled(tableAlias, fieldMeta);
    }

    @Override
    public void appendFieldPredicate(SpecialPredicate predicate) {
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
    public void appendTextValue(MappingMeta mappingType, Object value) {
        this.sqlBuilder
                .append(" ")
                .append(mappingType.nonNullTextValue(value));
    }

    @Override
    public final DQL dql() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
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
    public SimpleSQLWrapper build() {
        return SimpleSQLWrapper.build(this.sqlBuilder.toString(), this.paramList);
    }

    @Override
    public final TableContextSQLContext parentContext() {
        return this.parentContext;
    }

    /*################################## blow protected final method ##################################*/

    protected final void doAppendFiled(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        this.sqlBuilder.append(" ");
        if (this instanceof DeleteContext) {
            if (this.dialect.singleDeleteHasTableAlias()) {
                this.sqlBuilder.append(this.dialect.quoteIfNeed(tableAlias))
                        .append(".");
            }
        } else {
            this.sqlBuilder.append(this.dialect.quoteIfNeed(tableAlias))
                    .append(".");
        }
        this.sqlBuilder.append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }



    /*################################## blow protected template method ##################################*/
}
