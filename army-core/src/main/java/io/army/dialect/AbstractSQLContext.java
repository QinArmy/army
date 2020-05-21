package io.army.dialect;

import io.army.beans.DomainWrapper;
import io.army.criteria.FieldPairDualPredicate;
import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.wrapper.DomainSQLWrapper;
import io.army.wrapper.ParamWrapper;
import io.army.wrapper.SimpleSQLWrapper;

import java.util.ArrayList;
import java.util.List;

abstract class AbstractSQLContext implements TableContextSQLContext {

    protected final Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamWrapper> paramList;

    boolean finished;

    protected AbstractSQLContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder();
        this.paramList = new ArrayList<>();
    }

    protected AbstractSQLContext(TableContextSQLContext original) {
        this.dialect = original.dialect();
        this.visible = original.visible();
        this.sqlBuilder = original.sqlBuilder();
        this.paramList = original.paramList();
    }


    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        doAppendFiled(tableAlias, fieldMeta);
    }


    @Override
    public void appendFieldPair(FieldPairDualPredicate predicate) {
        predicate.left().appendSQL(this);
        this.sqlBuilder
                .append(" ")
                .append(predicate.operator().rendered());
        predicate.right().appendSQL(this);
    }

    @Override
    public void appendTable(TableMeta<?> tableMeta) {
        this.sqlBuilder
                .append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));
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
    public final SimpleSQLWrapper build() {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        return doBuild();
    }

    @Override
    public final DomainSQLWrapper build(DomainWrapper beanWrapper) {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        return doBuild(beanWrapper);
    }

    /*################################## blow protected final method ##################################*/

    protected final void doAppendFiled(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        this.sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    /*################################## blow protected template method ##################################*/

    protected SimpleSQLWrapper doBuild() {
        return SimpleSQLWrapper.build(
                sqlBuilder.toString()
                , paramList
        );
    }

    protected DomainSQLWrapper doBuild(DomainWrapper beanWrapper) {
        return DomainSQLWrapper.build(
                sqlBuilder.toString()
                , paramList
                , beanWrapper
        );
    }
}
