package io.army.dialect;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.Collections;
import java.util.List;

class StandardUpdateContext extends DefaultSQLContext implements UpdateContext {

    private static final InnerUpdate EMPTY_UPDATE = new InnerUpdate() {


        @Override
        public List<FieldMeta<?, ?>> targetFieldList() {
            return Collections.emptyList();
        }

        @Override
        public List<Expression<?>> valueExpList() {
            return Collections.emptyList();
        }

        @Override
        public void clear() {

        }

    };

    private final InnerUpdate update;

    StandardUpdateContext(DML dml, DQL dql, @Nullable InnerUpdate update) {
        super(dml, dql);

        InnerUpdate actualUpdate = update;
        if (actualUpdate == null) {
            actualUpdate = EMPTY_UPDATE;
        }
        this.update = actualUpdate;
    }

    @Override
    public final InnerUpdate innerUpdate() {
        return this.update;
    }
}
