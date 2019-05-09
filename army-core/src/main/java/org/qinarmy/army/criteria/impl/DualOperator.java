package org.qinarmy.army.criteria.impl;

/**
 * created  on 2018/11/25.
 */
enum DualOperator implements SqlOperator {

    EQ {
        @Override
        public DualOperator negated() {
            return NOT_EQ;
        }

        @Override
        public String rendered() {
            return "=";
        }

    },
    NOT_EQ {
        @Override
        public DualOperator negated() {
            return EQ;
        }

        @Override
        public String rendered() {
            return "!=";
        }


    },

    LT {
        @Override
        public DualOperator negated() {
            return GE;
        }

        @Override
        public String rendered() {
            return "<";
        }

    },
    LE {
        @Override
        public DualOperator negated() {
            return GT;
        }

        @Override
        public String rendered() {
            return "<=";
        }
    },
    GE {
        @Override
        public DualOperator negated() {
            return LT;
        }

        @Override
        public String rendered() {
            return ">=";
        }
    },
    GT {
        @Override
        public DualOperator negated() {
            return LE;
        }

        @Override
        public String rendered() {
            return ">";
        }
    };


    @Override
    public final Position position() {
        return Position.CENTER;
    }
}
