package io.army.dialect;

import io.army.beans.ReadonlyWrapper;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner.InnerStandardBatchInsert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.meta.TableMeta;

abstract class AbstractStandardInsertContext extends AbstractTableContextSQLContext implements InsertContext {

    static AbstractStandardInsertContext buildGeneric(Dialect dialect, Visible visible, ReadonlyWrapper readonlyWrapper
            , InnerStandardInsert insert) {
        return new StandardInsertContext(dialect, visible, insert.tableMeta(), readonlyWrapper);
    }

    static AbstractStandardInsertContext buildBatch(Dialect dialect, Visible visible, InnerStandardBatchInsert insert) {
        return new StandardBatchInsertContext(dialect, visible, insert.tableMeta());
    }


    private final StringBuilder fieldsBuilder = new StringBuilder();

    private final TableMeta<?> tableMeta;

    AbstractStandardInsertContext(Dialect dialect, Visible visible, TableMeta<?> tableMeta) {
        super(dialect, visible, null);
        this.tableMeta = tableMeta;
    }


    @Override
    public final StringBuilder fieldsBuilder() {
        return this.fieldsBuilder;
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
