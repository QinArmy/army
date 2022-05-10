package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.criteria.TableItem;
import io.army.criteria.impl.inner._NoTableBlock;
import io.army.criteria.impl.inner._Predicate;

import java.util.List;


public enum _JoinType implements SQLWords, _NoTableBlock {

    NONE(" "),
    LEFT_JOIN(" LEFT JOIN"),
    JOIN(" JOIN"),
    RIGHT_JOIN(" RIGHT JOIN"),
    FULL_JOIN(" FULL JOIN"),
    CROSS_JOIN(" CROSS JOIN"),

    /**
     * MySQL
     */
    STRAIGHT_JOIN(" STRAIGHT_JOIN");

    public final String keyWords;

    _JoinType(String keyWords) {
        this.keyWords = keyWords;
    }


    @Override
    public final String render() {
        return this.keyWords;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", _JoinType.class.getName(), this.name());
    }


    @Override
    public final TableItem tableItem() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final String alias() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final _JoinType jointType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final List<_Predicate> predicateList() {
        throw new UnsupportedOperationException();
    }


}
