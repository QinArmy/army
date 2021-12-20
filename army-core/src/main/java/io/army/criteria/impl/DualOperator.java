package io.army.criteria.impl;

import io.army.criteria.SQLOperator;

/**
 * Interface representing sql dual operator.
 */
enum DualOperator implements SQLOperator {

    PLUS {
        @Override
        public String rendered() {
            return "+";
        }
    }, MINUS {
        @Override
        public String rendered() {
            return "-";
        }
    }, MOD {
        @Override
        public String rendered() {
            return "%";
        }
    }, MULTIPLY {
        @Override
        public String rendered() {
            return "*";
        }
    }, DIVIDE {
        @Override
        public String rendered() {
            return "/";
        }
    }, AND {
        @Override
        public String rendered() {
            return "&";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    }, OR {
        @Override
        public String rendered() {
            return "|";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    }, XOR {
        @Override
        public String rendered() {
            return "^";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    }, LEFT_SHIFT {
        @Override
        public String rendered() {
            return "<<";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    }, RIGHT_SHIFT {
        @Override
        public String rendered() {
            return ">>";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    },
    /*################################## blow expression dual operator method ##################################*/

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
    }


}
