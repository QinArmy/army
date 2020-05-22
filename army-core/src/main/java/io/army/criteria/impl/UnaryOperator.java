package io.army.criteria.impl;

import io.army.criteria.SQLOperator;

/**
 * representing Unary SQL Operator
 */
enum UnaryOperator implements SQLOperator {

    NOT {

        @Override
        public String rendered() {
            return "NOT";
        }

        @Override
        public Position position() {
            return Position.LEFT;
        }
    },
    EXISTS {

        @Override
        public String rendered() {
            return "EXISTS";
        }

        @Override
        public Position position() {
            return Position.LEFT;
        }
    },
    NOT_EXISTS {

        @Override
        public String rendered() {
            return "NOT EXISTS";
        }

        @Override
        public Position position() {
            return Position.LEFT;
        }
    },
    NEGATED {

        @Override
        public String rendered() {
            return "-";
        }

        @Override
        public Position position() {
            return Position.LEFT;
        }
    },
    POSITIVE {

        @Override
        public String rendered() {
            return "";
        }

        @Override
        public Position position() {
            return Position.LEFT;
        }
    },
    IS_NULL {
        @Override
        public String rendered() {
            return "IS NULL";
        }

        @Override
        public Position position() {
            return Position.RIGHT;
        }
    },
    IS_NOT_NULL {

        @Override
        public String rendered() {
            return "IS NOT NULL";
        }

        @Override
        public Position position() {
            return Position.RIGHT;
        }
    }


}
