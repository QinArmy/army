package io.army.criteria.impl;

/**
 * created  on 2018/11/25.
 */
enum DualOperator implements SQLOperator {

    EQ {
        @Override
        public DualOperator negated() {
            return NOT_EQ;
        }

        @Override
        public String rendered() {
            return "=";
        }

    },
    NOT_EQ {
        @Override
        public DualOperator negated() {
            return EQ;
        }

        @Override
        public String rendered() {
            return "!=";
        }


    },

    LT {
        @Override
        public DualOperator negated() {
            return GE;
        }

        @Override
        public String rendered() {
            return "<";
        }

    },
    LE {
        @Override
        public DualOperator negated() {
            return GT;
        }

        @Override
        public String rendered() {
            return "<=";
        }
    },
    GE {
        @Override
        public DualOperator negated() {
            return LT;
        }

        @Override
        public String rendered() {
            return ">=";
        }
    },
    GT {
        @Override
        public DualOperator negated() {
            return LE;
        }

        @Override
        public String rendered() {
            return ">";
        }
    },
    IN {
        @Override
        public SQLOperator negated() {
            return NOT_IN;
        }

        @Override
        public String rendered() {
            return "IN";
        }
    },
    NOT_IN {
        @Override
        public SQLOperator negated() {
            return IN;
        }

        @Override
        public String rendered() {
            return "NOT IN";
        }
    },
    LIKE {
        @Override
        public SQLOperator negated() {
            return NOT_LIKE;
        }

        @Override
        public String rendered() {
            return "LIKE";
        }
    },
    NOT_LIKE {
        @Override
        public SQLOperator negated() {
            return LIKE;
        }

        @Override
        public String rendered() {
            return "NOT LIKE";
        }
    },
     ADD {
         @Override
         public SQLOperator negated() {
             return SUBTRACT;
         }

         @Override
         public String rendered() {
             return "+";
         }
     },SUBTRACT {
        @Override
        public SQLOperator negated() {
            return ADD;
        }

        @Override
        public String rendered() {
            return "-";
        }
    },MOD{
        @Override
        public SQLOperator negated() {
            return null;
        }

        @Override
        public String rendered() {
            return "%";
        }
    },MULTIPLY{
        @Override
        public SQLOperator negated() {
            return DIVIDE;
        }

        @Override
        public String rendered() {
            return "*";
        }
    },DIVIDE{
        @Override
        public SQLOperator negated() {
            return MULTIPLY;
        }

        @Override
        public String rendered() {
            return "/";
        }
    }

}
