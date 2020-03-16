package io.army.criteria;

public enum KeyOperator implements SQLOperator{

    ANY{
        @Override
        public String rendered() {
            return "ANY";
        }
    },
     SOME{
         @Override
         public String rendered() {
             return "SOME";
         }
     },
    ALL{
        @Override
        public String rendered() {
            return "ALL";
        }
    }


}
