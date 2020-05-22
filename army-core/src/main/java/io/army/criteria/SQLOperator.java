package io.army.criteria;


/**
 * Interface representing sql  operator.
 */
public interface SQLOperator {


    String rendered();

    default boolean relational() {
        return false;
    }

    default boolean bitOperator() {
        return false;
    }

    default Position position() {
        return Position.CENTER;
    }


    enum Position {
        LEFT,
        CENTER,
        RIGHT,
        TWO,
    }

}
