package io.army.dialect;

import io.army.criteria.Visible;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.modelgen._MetaBridge;
import io.army.sharding._RouteUtils;
import io.army.stmt.ParamValue;

import java.util.ArrayList;
import java.util.List;

public abstract class _BaseSqlContext implements _StmtContext {

    protected final Dialect dialect;

    protected final byte tableIndex;

    protected final String tableSuffix;

    protected final Visible visible;

    protected final StringBuilder sqlBuilder;

    protected final List<ParamValue> paramList;

    protected _BaseSqlContext(Dialect dialect, byte tableIndex, Visible visible) {
        this.dialect = dialect;
        this.tableIndex = tableIndex;
        this.visible = visible;
        this.tableSuffix = _RouteUtils.tableSuffix(tableIndex);

        this.sqlBuilder = new StringBuilder(128);
        this.paramList = new ArrayList<>();
    }


    @Override
    public final byte tableIndex() {
        return this.tableIndex;
    }

    @Override
    public final String tableSuffix() {
        return this.tableSuffix;
    }

    @Override
    public final Dialect dialect() {
        return this.dialect;
    }

    @Override
    public final StringBuilder sqlBuilder() {
        return this.sqlBuilder;
    }


    @Override
    public final void appendParam(ParamValue paramValue) {
        this.sqlBuilder.append(Constant.SPACE)
                .append(Constant.PLACEHOLDER);
        this.paramList.add(paramValue);
    }

    @Override
    public final Visible visible() {
        return this.visible;
    }


    protected static void childColumnFromSubQuery(final _Block parentContext, final _Block childBlock
            , final FieldMeta<?, ?> childField) {

        if (!(parentContext.table() instanceof ParentTableMeta)) {
            throw new IllegalArgumentException("parentContext error");
        }

        final String childSafeTableAlias = Constant.FORBID_ALIAS + "temp_c_of_" + childBlock.tableAlias();
        final Dialect dialect = parentContext.dialect();
        // convert for validate childBlock
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childBlock.table();

        final StringBuilder sqlBuilder = parentContext.sqlBuilder();
        sqlBuilder
                //below sub query left bracket
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET)

                .append(Constant.SPACE)
                .append(Constant.SELECT)
                .append(Constant.SPACE)

                .append(childSafeTableAlias)
                .append(Constant.POINT)
                .append(dialect.safeColumnName(childField.columnName()))

                .append(Constant.SPACE)
                .append(Constant.FROM);

        _RouteUtils.appendTableName(childTable, parentContext);

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.AS);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(childSafeTableAlias);

        sqlBuilder.append(Constant.SPACE)
                .append(Constant.WHERE)
                .append(Constant.SPACE)

                .append(childSafeTableAlias)
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE)
                .append(Constant.EQUAL)
                .append(Constant.SPACE)

                .append(parentContext.safeTableAlias())
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                //below sub query right bracket
                .append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);
    }


    protected static void parentColumnFromSubQuery(final _Block childContext, final FieldMeta<?, ?> parentField) {
        final ChildTableMeta<?> childTable = (ChildTableMeta<?>) childContext.table();
        final String parentSafeTable = Constant.FORBID_ALIAS + "temp_p_of_" + childContext.tableAlias();

        final Dialect dialect = childContext.dialect();
        final ParentTableMeta<?> parentTable = childTable.parentMeta();
        final StringBuilder sqlBuilder = childContext.sqlBuilder();

        sqlBuilder
                //below sub query left bracket
                .append(Constant.SPACE)
                .append(Constant.LEFT_BRACKET)

                .append(Constant.SPACE)
                .append(Constant.SELECT)
                .append(Constant.SPACE)

                //below target parent column
                .append(parentSafeTable)
                .append(Constant.POINT)
                .append(dialect.safeColumnName(parentField.columnName()))

                .append(Constant.SPACE)
                .append(Constant.FROM);

        // append parent table name
        _RouteUtils.appendTableName(parentTable, childContext);

        if (dialect.tableAliasAfterAs()) {
            sqlBuilder.append(Constant.SPACE)
                    .append(Constant.AS);
        }
        sqlBuilder.append(Constant.SPACE)
                .append(parentSafeTable);

        final FieldMeta<?, ?> discriminator = parentTable.discriminator();

        sqlBuilder.append(Constant.SPACE)
                //below where clause
                .append(Constant.WHERE)
                .append(Constant.SPACE)

                .append(parentSafeTable)
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE)
                .append(Constant.EQUAL)
                .append(Constant.SPACE)

                //below child table id
                .append(childContext.safeTableAlias())
                .append(Constant.POINT)
                .append(_MetaBridge.ID)

                .append(Constant.SPACE)
                .append(Constant.AND)
                .append(Constant.SPACE)

                //below parent table discriminator
                .append(parentSafeTable)
                .append(Constant.POINT)
                .append(dialect.safeColumnName(discriminator.columnName()))

                .append(Constant.SPACE)
                .append(Constant.EQUAL)
                .append(Constant.SPACE)

                //below child table discriminator literal
                .append(dialect.literal(discriminator, childTable.discriminatorValue()))

                //below sub query right bracket
                .append(Constant.SPACE)
                .append(Constant.RIGHT_BRACKET);


    }


}
