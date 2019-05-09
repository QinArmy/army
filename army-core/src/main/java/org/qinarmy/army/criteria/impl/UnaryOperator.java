package org.qinarmy.army.criteria.impl;

/**
 * created  on 2018/11/25.
 */
enum UnaryOperator implements SqlOperator {

    NOT {
        @Override
        public SqlOperator negated() {
            return null;
        }

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
        public UnaryOperator negated() {
            return NOT_EXISTS;
        }

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
        public UnaryOperator negated() {
            return EXISTS;
        }

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
        public UnaryOperator negated() {
            return POSITIVE;
        }

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
        public UnaryOperator negated() {
            return NEGATED;
        }

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
        public UnaryOperator negated() {
            return IS_NOT_NULL;
        }

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
        public UnaryOperator negated() {
            return IS_NULL;
        }

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
