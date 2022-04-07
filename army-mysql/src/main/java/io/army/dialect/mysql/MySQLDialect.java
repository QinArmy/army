package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._MySQLCounselor;
import io.army.criteria.impl.inner.mysql._IndexHint;
import io.army.criteria.impl.inner.mysql._MySQLHint;
import io.army.criteria.impl.inner.mysql._MySQLSingleUpdate;
import io.army.criteria.mysql.MySQLModifier;
import io.army.dialect.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

import java.util.List;

class MySQLDialect extends MySQL {

    private static final String SPACE_HINT_START = " /*+";

    private static final String SPACE_HINT_END = " */";

    private static final String SPACE_PARTITION_START = " PARTITION ( ";

    MySQLDialect(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
    }

    @Override
    protected final void assertDialectInsert(Insert insert) {
        super.assertDialectInsert(insert);
    }

    @Override
    protected final void assertDialectUpdate(Update update) {
        _MySQLCounselor.assertUpdate(update);
    }

    @Override
    protected final void assertDialectDelete(Delete delete) {
        super.assertDialectDelete(delete);
    }

    @Override
    protected final void assertDialectQuery(Query query) {
        super.assertDialectQuery(query);
    }


    @Override
    protected SimpleStmt dialectSingleUpdate(final _SingleUpdateContext context) {
        final _MySQLSingleUpdate stmt = (_MySQLSingleUpdate) context.statement();
        final _Dialect dialect = context.dialect();
        final StringBuilder sqlBuilder = context.sqlBuilder()
                //1. UPDATE key word
                .append(Constant.UPDATE);

        //2. hint comment block
        hintClause(stmt.hintList(), sqlBuilder, context);
        //3. modifier
        for (MySQLModifier modifier : stmt.modifierList()) {
            switch (modifier) {
                case LOW_PRIORITY:
                case IGNORE:
                    sqlBuilder.append(modifier.render());
                    break;
                default:
                    throw _Exceptions.commandAndModifierNotMatch(stmt, modifier);

            }
        }

        //4. table name
        final TableMeta<?> table = context.table();
        final SingleTableMeta<?> targetTable;
        final String safeTableAlias = context.safeTableAlias();
        if (table instanceof ChildTableMeta) {
            targetTable = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            targetTable = (SingleTableMeta<?>) table;
        }
        sqlBuilder.append(Constant.SPACE);
        dialect.safeObjectName(targetTable.tableName(), sqlBuilder);

        //5. partition
        this.partitionClause(stmt.partitionList(), sqlBuilder, dialect);
        //6. table alias

        sqlBuilder.append(Constant.SPACE_AS_SPACE)
                .append(safeTableAlias);

        //7. index hint
        this.indexHintClause(stmt.indexHintList(), sqlBuilder, dialect);
        //8. set clause
        final List<TableField<?>> conditionFields;
        conditionFields = this.singleTableSetClause(true, context);
        //9. where clause
        this.dmlWhereClause(context);
        //9.1 discriminator
        if (!(table instanceof SimpleTableMeta)) {
            this.discriminator(table, safeTableAlias, context);
        }
        //9.2 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(conditionFields, context);
        }
        //9.3 append visible
        if (targetTable.containField(_MetaBridge.VISIBLE)) {
            this.visiblePredicate(targetTable, safeTableAlias, context);
        }

        //10. order by clause
        this.orderByClause(stmt.orderByList(), context);
        //11. limit clause
        final long rowCount;
        rowCount = stmt.rowCount();
        if (rowCount >= 0) {
            sqlBuilder.append(Constant.SPACE_LIMIT_SPACE)
                    .append(rowCount);
        }
        return context.build();
    }


    private void hintClause(List<_MySQLHint> hintList, final StringBuilder sqlBuilder
            , final _SqlContext context) {
        if (hintList.size() == 0) {
            return;
        }
        sqlBuilder.append(SPACE_HINT_START);
        for (_MySQLHint hint : hintList) {
            _MySQLCounselor.assertHint(hint);
            hint.appendSql(context);
        }
        sqlBuilder.append(SPACE_HINT_END);
    }

    private void partitionClause(final List<String> partitionList, final StringBuilder sqlBuilder
            , final _Dialect dialect) {
        final int partitionSize = partitionList.size();
        if (partitionSize == 0) {
            return;
        }
        sqlBuilder.append(SPACE_PARTITION_START);
        for (int i = 0; i < partitionSize; i++) {
            if (i > 0) {
                sqlBuilder.append(Constant.SPACE_COMMA_SPACE);
            }
            dialect.quoteIfNeed(partitionList.get(i), sqlBuilder);
        }
        sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);
    }

    private void indexHintClause(List<? extends _IndexHint> indexHintList, final StringBuilder sqlBuilder
            , final _Dialect dialect) {
        if (indexHintList.size() == 0) {
            return;
        }
        SQLModifier purpose;
        List<String> indexNameList;
        int indexSize;
        for (_IndexHint indexHint : indexHintList) {
            sqlBuilder.append(indexHint.command().render());
            purpose = indexHint.purpose();
            if (purpose != null) {
                sqlBuilder.append(purpose.render());
            }
            indexNameList = indexHint.indexNameList();
            indexSize = indexNameList.size();
            assert indexSize > 0;
            sqlBuilder.append(Constant.SPACE_LEFT_BRACKET);
            for (int i = 0; i < indexSize; i++) {
                if (i > 0) {
                    sqlBuilder.append(Constant.SPACE_COMMA_SPACE);
                }
                dialect.quoteIfNeed(indexNameList.get(i), sqlBuilder);
            }
            sqlBuilder.append(Constant.SPACE_RIGHT_BRACKET);
        }

    }


}
