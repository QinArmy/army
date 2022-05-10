package io.army.dialect.mysql;

import io.army.criteria.*;
import io.army.criteria.impl._MySQLCounselor;
import io.army.criteria.impl.inner._DialectStatement;
import io.army.criteria.impl.inner._TableBlock;
import io.army.criteria.impl.inner.mysql.*;
import io.army.criteria.mysql.MySQLWords;
import io.army.dialect.*;
import io.army.meta.ChildTableMeta;
import io.army.meta.SimpleTableMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.stmt.SimpleStmt;
import io.army.util._Exceptions;

import java.util.List;

/**
 * <p>
 * This class is the implementation of {@link _Dialect} for  MySQL dialect criteria api.
 * </p>
 * <p>
 * Below is chinese signature:<br/>
 * 当你在阅读这段代码时,我才真正在写这段代码,你阅读到哪里,我便写到哪里.
 * </p>
 *
 * @since 1.0
 */
final class MySQLDialect extends MySQL {

    private static final String SPACE_HINT_START = " /*+";

    private static final String SPACE_HINT_END = " */";

    private static final String SPACE_PARTITION_START = " PARTITION ( ";

    MySQLDialect(_DialectEnvironment environment, Dialect dialect) {
        super(environment, dialect);
    }

    @Override
    protected void assertDialectInsert(Insert insert) {
        super.assertDialectInsert(insert);
    }

    @Override
    protected void assertDialectUpdate(Update update) {
        _MySQLCounselor.assertUpdate(update);
    }

    @Override
    protected void assertDialectDelete(Delete delete) {
        super.assertDialectDelete(delete);
    }

    @Override
    protected void assertDialectQuery(Query query) {
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
        this.updateModifiers(stmt.modifierList(), sqlBuilder, stmt);

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

    @Override
    protected SimpleStmt dialectMultiUpdate(final _MultiUpdateContext context) {
        final _MySQLMultiUpdate stmt = (_MySQLMultiUpdate) context.statement();
        final StringBuilder sqlBuilder = context.sqlBuilder()
                //1. UPDATE key word
                .append(Constant.UPDATE);

        //2. hint comment block
        this.hintClause(stmt.hintList(), sqlBuilder, context);
        //3. modifier
        this.updateModifiers(stmt.modifierList(), sqlBuilder, stmt);
        //4. table_references (and partition ,index hint)
        this.standardFromClause(stmt.tableBlockList(), context);
        //5. set clause
        final List<TableField<?>> conditionFields;
        conditionFields = this.multiTableSetClause(context);
        //6. where clause
        this.dmlWhereClause(context);
        //6.2 append condition update fields
        if (conditionFields.size() > 0) {
            this.conditionUpdate(conditionFields, context);
        }
        //6.3 append visible
        this.multiDmlVisible(stmt.tableBlockList(), context);
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

    private void updateModifiers(List<MySQLWords> modifierSet, StringBuilder builder, _DialectStatement stmt) {
        for (MySQLWords modifier : modifierSet) {
            switch (modifier) {
                case LOW_PRIORITY:
                case IGNORE:
                    builder.append(modifier.render());
                    break;
                default:
                    throw _Exceptions.commandAndModifierNotMatch(stmt, modifier);

            }
        }
    }


    private void mysqlDmlTableReferences(final List<? extends _TableBlock> blockList, final _StmtContext context) {
        final int blockSize = blockList.size();
        if (blockSize == 0) {
            throw new CriteriaException("No table_references");
        }
        final StringBuilder sqlBuilder = context.sqlBuilder();
        final _Dialect dialect = context.dialect();
        _TableBlock block;
        _MySQLTableBlock mySqlBlock = null;
        TableItem tableItem;
        TableMeta<?> table;
        String safeTableAlias;
        for (int i = 0; i < blockSize; i++) {
            block = blockList.get(i);
            if (i > 0) {
                sqlBuilder.append(block.jointType().render());
            }
            tableItem = block.tableItem();
            if (tableItem instanceof SubQuery) {
                dialect.subQuery((SubQuery) tableItem, context);
                sqlBuilder.append(Constant.SPACE_AS_SPACE);
                dialect.quoteIfNeed(block.alias(), sqlBuilder);
            } else if (tableItem instanceof TableMeta) {
                sqlBuilder.append(Constant.SPACE);
                table = (TableMeta<?>) tableItem;
                dialect.safeObjectName(table.tableName(), sqlBuilder);
                if (block instanceof _MySQLTableBlock) {
                    mySqlBlock = (_MySQLTableBlock) block;
                    this.partitionClause(mySqlBlock.partitionList(), sqlBuilder, dialect);
                }
                safeTableAlias = context.safeTableAlias(table, block.alias());
                sqlBuilder.append(Constant.SPACE_AS_SPACE)
                        .append(safeTableAlias);

                if (mySqlBlock == block) {
                    this.indexHintClause(mySqlBlock.indexHintList(), sqlBuilder, dialect);
                }


            }

        }
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
        SQLWords purpose;
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
