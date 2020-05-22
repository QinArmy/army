package io.army.criteria;

public enum DualPredicateOperator implements SQLOperator {

    EQ {
        @Override
        public String rendered() {
            return "=";
        }

        @Override
        public boolean relational() {
            return true;
        }
    },
    NOT_EQ {
        @Override
        public String rendered() {
            return "!=";
        }

        @Override
        public boolean relational() {
            return true;
        }
    },
    LT {
        @Override
        public String rendered() {
            return "<";
        }

        @Override
        public boolean relational() {
            return true;
        }
    },
    LE {
        @Override
        public String rendered() {
            return "<=";
        }

        @Override
        public boolean relational() {
            return true;
        }

    },
    GE {
        @Override
        public String rendered() {
            return ">=";
        }

        @Override
        public boolean relational() {
            return true;
        }

    },
    GT {
        @Override
        public String rendered() {
            return ">";
        }

        @Override
        public boolean relational() {
            return true;
        }

    }, IN {
        @Override
        public String rendered() {
            return "IN";
        }
    },
    NOT_IN {
        @Override
        public String rendered() {
            return "NOT IN";
        }
    },
    LIKE {
        @Override
        public String rendered() {
            return "LIKE";
        }
    },
    NOT_LIKE {
        @Override
        public String rendered() {
            return "NOT LIKE";
        }
    };


}
