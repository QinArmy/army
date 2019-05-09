package org.qinarmy.army.criteria.impl;

/**
 * created  on 2018/11/25.
 */
public enum TernaryOperator implements SqlOperator {

    BETWEEN {
        @Override
        public TernaryOperator negated() {
            return null;
        }

        @Override
        public String rendered() {
            return "%s BETWEEN %s AND %s";
        }
    };


    @Override
    public final Position position() {
        return Position.TOW;
    }
}
