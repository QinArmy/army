package io.army.criteria.impl;

import io.army.criteria.IPredicate;
import io.army.criteria.OrPredicate;
import io.army.criteria.impl.inner._Predicate;
import io.army.dialect._SqlContext;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class OrtPredicateImpl extends AbstractPredicate implements OrPredicate {

    private final _Predicate leftPredicate;

    private final List<_Predicate> rightPredicate;

    OrtPredicateImpl(_Predicate leftPredicate, IPredicate... andPredicates) {
        this.leftPredicate = leftPredicate;
        if (andPredicates.length == 1) {
            this.rightPredicate = Collections.singletonList((_Predicate) andPredicates[0]);
        } else {
            final List<_Predicate> tempList = new ArrayList<>(andPredicates.length);
            for (IPredicate predicate : andPredicates) {
                tempList.add((_Predicate) predicate);
            }
            this.rightPredicate = Collections.unmodifiableList(tempList);
        }

    }

    OrtPredicateImpl(_Predicate leftPredicate, List<IPredicate> predicateList) {
        this.leftPredicate = leftPredicate;

        final List<_Predicate> tempList = new ArrayList<>(predicateList.size());
        for (IPredicate predicate : predicateList) {
            tempList.add((_Predicate) predicate);
        }
        this.rightPredicate = Collections.unmodifiableList(tempList);
    }


    @Override
    public IPredicate leftPredicate() {
        return this.leftPredicate;
    }

    @Override
    public List<IPredicate> rightPredicate() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendSql(_SqlContext context) {
        StringBuilder builder = context.sqlBuilder()
                .append(" (");
        this.leftPredicate.appendSql(context);
        builder.append(" OR");
        if (this.rightPredicate.size() > 1) {
            builder.append(" (");
        }
        int index = 0;
        for (_Predicate predicate : this.rightPredicate) {
            if (index > 0) {
                builder.append(" AND");
            }
            predicate.appendSql(context);
            index++;
        }
        if (this.rightPredicate.size() > 1) {
            builder.append(" )");
        }
        builder.append(" )");
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        builder.append(leftPredicate)
                .append(" OR ")
        ;
        if (rightPredicate.size() > 1) {
            builder.append(" (");
        }
        int index = 0;
        for (IPredicate predicate : rightPredicate) {
            if (index > 0) {
                builder.append(" AND");
            }
            builder.append(predicate);
            index++;
        }
        if (rightPredicate.size() > 1) {
            builder.append(" )");
        }
        builder.append(" )");
        return builder.toString();
    }

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        boolean contains = this.leftPredicate.containsField(fieldMetas);
        if (!contains) {
            for (_Predicate predicate : this.rightPredicate) {
                if (predicate.containsField(fieldMetas)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        boolean contains = this.leftPredicate.containsFieldOf(tableMeta);
        if (!contains) {
            for (_Predicate predicate : this.rightPredicate) {
                if (predicate.containsFieldOf(tableMeta)) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        int count = this.leftPredicate.containsFieldCount(tableMeta);
        for (_Predicate predicate : this.rightPredicate) {
            count += predicate.containsFieldCount(tableMeta);
        }
        return count;
    }

    @Override
    public boolean containsSubQuery() {
        boolean contains = this.leftPredicate.containsSubQuery();
        if (!contains) {
            for (_Predicate predicate : this.rightPredicate) {
                if (predicate.containsSubQuery()) {
                    contains = true;
                    break;
                }
            }
        }
        return contains;
    }
}
