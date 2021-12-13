package io.army.criteria.impl;

import io.army.criteria.FieldPredicate;
import io.army.criteria.IPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * this interface representing a {@code not} expression
 */
class NotPredicateImpl extends AbstractPredicate {

    static IPredicate build(final _Predicate predicate) {
        _Predicate notPredicate;
        if (predicate instanceof NotPredicateImpl) {
            notPredicate = ((NotPredicateImpl) predicate).predicate;
        } else if (predicate instanceof FieldPredicate) {
            notPredicate = new FieldNotPredicate((FieldPredicate) predicate);
        } else {
            notPredicate = new NotPredicateImpl(predicate);
        }
        return notPredicate;
    }

    final _Predicate predicate;

    private NotPredicateImpl(_Predicate predicate) {
        this.predicate = predicate;

    }

    @Override
    public void appendSql(_SqlContext context) {
        doAppendSQL(context);
    }

    final void doAppendSQL(_SqlContext context) {
        context.sqlBuilder()
                .append(" ")
                .append(UnaryOperator.NOT.rendered());
        this.predicate.appendSql(context);
    }

    @Override
    public final boolean containsSubQuery() {
        return this.predicate.containsSubQuery();
    }

    @Override
    public String toString() {
        return UnaryOperator.NOT.rendered() + " " + predicate;
    }

    /*################################## blow private static inner class  ##################################*/


    private static final class FieldNotPredicate extends NotPredicateImpl implements FieldPredicate {

        private FieldNotPredicate(FieldPredicate predicate) {
            super((_Predicate) predicate);
        }


        @Override
        public void appendSql(_SqlContext context) {
            context.appendFieldPredicate(this);
        }


        //@Override
        public void appendPredicate(_SqlContext context) {
            this.doAppendSQL(context);
        }

        @Override
        public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
            return this.predicate.containsField(fieldMetas);
        }

        @Override
        public boolean containsFieldOf(TableMeta<?> tableMeta) {
            return this.predicate.containsFieldOf(tableMeta);
        }

        @Override
        public int containsFieldCount(TableMeta<?> tableMeta) {
            return this.predicate.containsFieldCount(tableMeta);
        }
    }


}
