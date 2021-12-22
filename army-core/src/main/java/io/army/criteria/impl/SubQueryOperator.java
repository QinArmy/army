package io.army.criteria.impl;


enum SubQueryOperator {

    ANY {
        @Override
        public String rendered() {
            return "ANY";
        }
    },
    SOME {
        @Override
        public String rendered() {
            return "SOME";
        }
    },
    ALL {
        @Override
        public String rendered() {
            return "ALL";
        }
    };

    abstract String rendered();

}


