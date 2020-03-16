package io.army.criteria;

/**
 * created  on 2018/11/25.
 */
public enum DualOperator implements SQLOperator {

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
    },
    IN {

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
    },
     ADD {

         @Override
         public String rendered() {
             return "+";
         }
     },SUBTRACT {

        @Override
        public String rendered() {
            return "-";
        }
    },MOD{

        @Override
        public String rendered() {
            return "%";
        }
    },MULTIPLY{

        @Override
        public String rendered() {
            return "*";
        }
    },DIVIDE{

        @Override
        public String rendered() {
            return "/";
        }
    },AND {

        @Override
        public String rendered() {
            return "&";
        }
    },OR {
        @Override
        public String rendered() {
            return "|";
        }
    },XOR {
        @Override
        public String rendered() {
            return "^";
        }
    },LEFT_SHIFT{

        @Override
        public String rendered() {
            return "<<";
        }
    },RIGHT_SHIFT{
        @Override
        public String rendered() {
            return ">>";
        }
    },INVERT{
        @Override
        public String rendered() {
            return "~";
        }
    }



}
