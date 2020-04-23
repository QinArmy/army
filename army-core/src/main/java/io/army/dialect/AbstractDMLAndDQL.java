package io.army.dialect;

import io.army.criteria.Visible;
import io.army.criteria.impl.SQLS;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.Random;

public abstract class AbstractDMLAndDQL extends AbstractSQL {

    protected AbstractDMLAndDQL(Dialect dialect) {
        super(dialect);
    }

    protected String subQueryParentAlias(String parentTableName) {
        Random random = new Random();
        return "_" + parentTableName + random.nextInt(4) + "_";
    }

    protected abstract boolean tableAliasAfterAs();


    /*################################## blow final protected method ##################################*/

    protected final void visibleConstantPredicate(ClauseSQLContext context
            , TableMeta<?> tableMeta, String tableAlias) {

        switch (context.visible()) {
            case ONLY_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.TRUE, tableMeta, tableAlias);
                break;
            case ONLY_NON_VISIBLE:
                doVisibleConstantPredicate(context, Boolean.FALSE, tableMeta, tableAlias);
                break;
            case BOTH:
                break;
            default:
                throw new IllegalArgumentException(String.format("unknown Visible[%s]", context.visible()));
        }
    }


    protected final void visibleSubQueryPredicateForChild(ClauseSQLContext context
            , ChildTableMeta<?> childMeta, String childAlias) {
        if (context.visible() == Visible.BOTH) {
            return;
        }

        ParentTableMeta<?> parentMeta = childMeta.parentMeta();

        final String parentAlias = subQueryParentAlias(parentMeta.tableName());
        // append exists SubQuery
        StringBuilder builder = context.sqlBuilder()
                .append(" ")
                .append(Keywords.AND)
                .append(" ")
                .append(Keywords.EXISTS)
                .append(" ( ")
                .append(Keywords.SELECT);

        context.appendField(parentAlias, parentMeta.primaryKey());
        // from clause
        builder.append(" ")
                .append(Keywords.FROM);
        context.appendParentTableOf(childMeta);

        if (tableAliasAfterAs()) {
            builder.append(" ")
                    .append(Keywords.AS);
        }
        context.appendText(parentAlias);
        // where clause
        builder.append(" ")
                .append(Keywords.WHERE);
        context.appendField(parentAlias, parentMeta.primaryKey());
        builder.append(" =");

        context.appendField(childAlias, childMeta.primaryKey());

        // visible predicate
        visibleConstantPredicate(context, childMeta, childAlias);
        builder.append(" )");
    }


    private void doVisibleConstantPredicate(ClauseSQLContext context, Boolean visible
            , TableMeta<?> tableMeta, String tableAlias) {

        final FieldMeta<?, ?> visibleField = tableMeta.getField(TableMeta.VISIBLE);

        StringBuilder builder = context.sqlBuilder()
                .append(" AND");

        context.appendField(tableAlias, visibleField);

        builder.append(" =");
        SQLS.constant(visible, visibleField.mappingType())
                .appendSQL(context);

    }


}
