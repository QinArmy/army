package io.army.criteria.impl;

import io.army.criteria.SQLOperator;

enum SubQueryOperator implements SQLOperator {

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
    }, EXISTS {
        @Override
        public String rendered() {
            return "EXISTS";
        }
    }, NOT_EXISTS {
        @Override
        public String rendered() {
            return "NOT EXISTS";
        }
    }, IN {
        @Override
        public String rendered() {
            return "IN";
        }
    }, NOT_IN {
        @Override
        public String rendered() {
            return "NOT IN";
        }
    };

}


