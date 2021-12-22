package io.army.criteria.impl;


/**
 * representing Unary SQL Operator
 */
enum UnaryOperator {

    EXISTS {
        @Override
        public String rendered() {
            return "EXISTS";
        }

    },
    NOT_EXISTS {

        @Override
        public String rendered() {
            return "NOT EXISTS";
        }

    },
    NEGATED {

        @Override
        public String rendered() {
            return "-";
        }

    },
    POSITIVE {

        @Override
        public String rendered() {
            return "";
        }

    },
    IS_NULL {
        @Override
        public String rendered() {
            return "IS NULL";
        }

    },
    IS_NOT_NULL {
        @Override
        public String rendered() {
            return "IS NOT NULL";
        }

    }, INVERT {
        @Override
        public String rendered() {
            return "~";
        }

    };

    abstract String rendered();


}
