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
    }

}


