package io.army.criteria.impl;


enum SubQueryOperator {

    ANY {
        @Override
        public String rendered() {
            return " ANY";
        }
    },
    SOME {
        @Override
        public String rendered() {
            return " SOME";
        }
    },
    ALL {
        @Override
        public String rendered() {
            return " ALL";
        }
    };

    /**
     * @return one space char and operator
     */
    abstract String rendered();

}


