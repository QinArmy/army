package io.army.criteria.impl;


/**
 * Interface representing sql dual operator.
 */
enum DualOperator {

    PLUS(" +"),
    MINUS(" -"),
    MOD(" %"),
    MULTIPLY(" *"),
    DIVIDE(" /"),
    BITWISE_AND(" &"),
    BITWISE_OR(" |"),
    XOR(" ^"),
    LEFT_SHIFT(" <<"),
    RIGHT_SHIFT(" >>"),
    /*################################## blow expression dual operator method ##################################*/

    EQUAL(" ="),
    NOT_EQUAL(" !="),
    LESS(" <"),
    LESS_EQUAL(" <="),
    GREAT_EQUAL(" >="),
    GREAT(" >"),
    IN(" IN"),
    NOT_IN(" NOT IN"),
    LIKE(" LIKE"),
    NOT_LIKE(" NOT LIKE");


    final String signText;

    /**
     * @param signText space and sign
     */
    DualOperator(String signText) {
        this.signText = signText;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", DualOperator.class.getName(), this.name());
    }


}
