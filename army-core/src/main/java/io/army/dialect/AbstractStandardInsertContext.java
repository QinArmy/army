package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.meta.TableMeta;
import io.army.util.Assert;

abstract class AbstractStandardInsertContext extends AbstractClauseContext implements InsertContext {

    static AbstractStandardInsertContext buildGeneral(Dialect dialect, Visible visible, ReadonlyWrapper readonlyWrapper
            , InnerStandardInsert insert) {
        return new StandardInsertContext(dialect, visible, insert.tableMeta(), readonlyWrapper);
    }

    static AbstractStandardInsertContext buildBatch(Dialect dialect, Visible visible, InnerStandardBatchInsert insert) {
        return new StandardBatchInsertContext(dialect, visible, insert.tableMeta());
    }


    private final StringBuilder fieldsBuilder = new StringBuilder();

    private final TableMeta<?> tableMeta;

    AbstractStandardInsertContext(Dialect dialect, Visible visible, TableMeta<?> tableMeta) {
        super(dialect, visible);
        this.tableMeta = tableMeta;
    }

    @Override
    public final void appendTable(TableMeta<?> tableMeta) {
        if (tableMeta != this.tableMeta) {
            throw DialectUtils.createArmyCriteriaException();
        }
        this.fieldsBuilder.append(" ")
                .append(this.dialect.quoteIfNeed(tableMeta.tableName()));
        appendTableSuffix();
    }

    @Override
    public final StringBuilder fieldsBuilder() {
        return this.fieldsBuilder;
    }

    @Override
    public final void currentClause(Clause clause) {
        Clause lastClause = null;
        if (!clauseStack.empty()) {
            lastClause = clauseStack.peek();
        }
        switch (clause) {
            case INSERT_INTO:
                Assert.state(lastClause == null, "Clause error.");
                clauseStack.push(Clause.INSERT_INTO);
                fieldsBuilder.append(Keywords.INSERT_INTO);
                break;
            case VALUE:
                Assert.state(lastClause == Clause.INSERT_INTO, "Clause error.");
                clauseStack.pop();
                clauseStack.push(Clause.VALUE);
                sqlBuilder
                        .append(" ")
                        .append(Keywords.VALUE);
                break;
            case VALUES:
                Assert.state(lastClause == Clause.INSERT_INTO, "Clause error.");
                clauseStack.pop();
                clauseStack.push(Clause.VALUES);
                sqlBuilder
                        .append(" ")
                        .append(Keywords.VALUES);
                break;
            default:
                throw DialectUtils.createNotSupportClauseException(this, clause);
        }
    }

    void appendTableSuffix() {

    }

    private static final class StandardInsertContext extends AbstractStandardInsertContext {

        private final ReadonlyWrapper readonlyWrapper;

        StandardInsertContext(Dialect dialect, Visible visible, TableMeta<?> tableMeta
                , ReadonlyWrapper readonlyWrapper) {
            super(dialect, visible, tableMeta);
            this.readonlyWrapper = readonlyWrapper;
        }
    }

    private static final class StandardBatchInsertContext extends AbstractStandardInsertContext {

        private StandardBatchInsertContext(Dialect dialect, Visible visible, TableMeta<?> tableMeta) {
            super(dialect, visible, tableMeta);
        }


    }
}
