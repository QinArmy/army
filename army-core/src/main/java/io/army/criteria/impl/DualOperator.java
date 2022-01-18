package io.army.criteria.impl;


/**
 * Interface representing sql dual operator.
 */
enum DualOperator {

    PLUS {
        @Override
        public String rendered() {
            return " +";
        }
    }, MINUS {
        @Override
        public String rendered() {
            return " -";
        }
    }, MOD {
        @Override
        public String rendered() {
            return " %";
        }
    }, MULTIPLY {
        @Override
        public String rendered() {
            return " *";
        }
    }, DIVIDE {
        @Override
        public String rendered() {
            return " /";
        }
    }, AND {
        @Override
        public String rendered() {
            return " &";
        }

    }, OR {
        @Override
        public String rendered() {
            return " |";
        }

    }, XOR {
        @Override
        public String rendered() {
            return " ^";
        }

    }, LEFT_SHIFT {
        @Override
        public String rendered() {
            return " <<";
        }

    }, RIGHT_SHIFT {
        @Override
        public String rendered() {
            return " >>";
        }

    },
    /*################################## blow expression dual operator method ##################################*/

    EQ {
        @Override
        public String rendered() {

            return " =";
        }

    },
    NOT_EQ {
        @Override
        public String rendered() {
            return " !=";
        }
    },
    LT {
        @Override
        public String rendered() {
            return " <";
        }
    },
    LE {
        @Override
        public String rendered() {
            return " <=";
        }

    },
    GE {
        @Override
        public String rendered() {
            return " >=";
        }

    },
    GT {
        @Override
        public String rendered() {
            return " >";
        }

    }, IN {
        @Override
        public String rendered() {
            return " IN";
        }
    },
    NOT_IN {
        @Override
        public String rendered() {
            return " NOT IN";
        }
    },
    LIKE {
        @Override
        public String rendered() {
            return " LIKE";
        }
    },
    NOT_LIKE {
        @Override
        public String rendered() {
            return " NOT LIKE";
        }
    };


    /**
     * @return one space char and operator
     */
    abstract String rendered();


}
