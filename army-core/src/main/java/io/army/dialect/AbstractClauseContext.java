package io.army.dialect;

import io.army.beans.BeanWrapper;
import io.army.criteria.FieldPairDualPredicate;
import io.army.criteria.TableAliasException;
import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class AbstractClauseContext implements ClauseSQLContext {

    protected final Dialect dialect;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamWrapper> paramList = new ArrayList<>();

    protected final Stack<Clause> clauseStack = new Stack<>();

    boolean finished;

    protected AbstractClauseContext(Dialect dialect, Visible visible) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = new StringBuilder();
    }

    protected AbstractClauseContext(Dialect dialect, Visible visible, StringBuilder sqlBuilder) {
        this.dialect = dialect;
        this.visible = visible;
        this.sqlBuilder = sqlBuilder;
    }

    @Override
    public void appendField(String tableAlias, FieldMeta<?, ?> fieldMeta) throws TableAliasException {
        sqlBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableAlias))
                .append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
    }

    @Override
    public void appendField(FieldMeta<?, ?> fieldMeta) {
        appendTable(fieldMeta.tableMeta());
        sqlBuilder.append(".")
                .append(this.dialect.quoteIfNeed(fieldMeta.fieldName()));
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
    public void appendParentTableOf(ChildTableMeta<?> childTableMeta) {
        appendTable(childTableMeta.parentMeta());
    }

    @Override
    public final void appendText(String textValue) {
        sqlBuilder.append(" ")
                .append(dialect.quoteIfNeed(textValue));
    }

    @Override
    public final void appendTextValue(MappingType mappingType, Object value) {
        sqlBuilder.append(
                DialectUtils.quoteIfNeed(
                        mappingType
                        , mappingType.nonNullTextValue(value)
                )
        );
    }

    @Override
    public final DQL dql() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return sqlBuilder;
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
    public final SQLWrapper build() {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        clauseStack.clear();
        return doBuild();
    }

    @Override
    public final BeanSQLWrapper build(BeanWrapper beanWrapper) {
        Assert.state(!this.finished, "SQLContext finished.");
        this.finished = true;
        clauseStack.clear();
        return doBuild(beanWrapper);
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


    protected SQLWrapper doBuild() {
        return SQLWrapper.build(
                sqlBuilder.toString()
                , paramList
        );
    }

    protected BeanSQLWrapper doBuild(BeanWrapper beanWrapper) {
        return BeanSQLWrapper.build(
                sqlBuilder.toString()
                , paramList
                , beanWrapper
        );
    }

}
