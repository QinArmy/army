package io.army.dialect;

import io.army.criteria.Expression;
import io.army.criteria.impl.SQLS;
import io.army.criteria.impl.inner.InnerInsert;
import io.army.criteria.impl.inner.InnerStandardInsert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

class StandardInsertContext extends DefaultSQLContext implements InsertContext {

    private static final InnerInsert EMPTY_INSERT = new InnerInsert() {
        @Override
        public List<FieldMeta<?, ?>> fieldList() {
            return Collections.emptyList();
        }

        @Override
        public TableMeta<?> tableMeta() {
            return SQLS.dual();
        }

        @Override
        public void clear() {

        }
    };

    private final StringBuilder fieldStringBuilder = new StringBuilder();

    private final boolean defaultIfNull;

    private final Map<FieldMeta<?, ?>, Expression<?>> commonValueMap;

    private final InnerInsert innerInsert;

    StandardInsertContext(DML dml, DQL dql, InnerInsert innerInsert) {
        super(dml, dql);
        this.innerInsert = innerInsert;
        if (innerInsert instanceof InnerStandardInsert) {
            InnerStandardInsert insert = (InnerStandardInsert) innerInsert;
            this.defaultIfNull = insert.defaultExpIfNull();
            this.commonValueMap = insert.commonValueMap();
        } else {
            this.defaultIfNull = false;
            this.commonValueMap = Collections.emptyMap();
        }
    }

    StandardInsertContext(DML dml, DQL dql) {
        super(dml, dql);
        this.innerInsert = EMPTY_INSERT;
        this.defaultIfNull = false;
        this.commonValueMap = Collections.emptyMap();
    }

    @Override
    public final StringBuilder fieldStringBuilder() {
        return this.fieldStringBuilder;
    }

    @Nullable
    @Override
    public final Expression<?> commonExp(FieldMeta<?, ?> fieldMeta) {
        return this.commonValueMap.get(fieldMeta);
    }

    @Override
    public final boolean defaultIfNull() {
        return this.defaultIfNull;
    }

    @Override
    public final InnerInsert innerInsert() {
        return this.innerInsert;
    }
}
