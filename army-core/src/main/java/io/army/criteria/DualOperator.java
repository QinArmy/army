package io.army.criteria;

/**
 * Interface representing sql dual operator.
 */
public enum DualOperator implements SQLOperator {

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

        @Override
        public boolean bitOperator() {
            return true;
        }
    },OR {
        @Override
        public String rendered() {
            return "|";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    },XOR {
        @Override
        public String rendered() {
            return "^";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    },LEFT_SHIFT {
        @Override
        public String rendered() {
            return "<<";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    },RIGHT_SHIFT {
        @Override
        public String rendered() {
            return ">>";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    },INVERT {
        @Override
        public String rendered() {
            return "~";
        }

        @Override
        public boolean bitOperator() {
            return true;
        }
    }


}
