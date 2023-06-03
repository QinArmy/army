package io.army.criteria.impl;

import io.army.dialect.Database;

/**
 * Package interface
 */
interface Operator {

    String name();

    @Deprecated
    String spaceRender();

    /**
     * @throws UnsupportedOperationException throw when this is standard operator enum :
     *                                       <ul>
     *                                           <li>{@link UnaryExpOperator}</li>
     *                                           <li>{@link UnaryExpOperator}</li>
     *                                           <li>{@link SqlDualBooleanOperator} type</li>
     *                                       </ul>
     */
    @Deprecated
    Database database();

    default String spaceRender(Database database) {
        throw new UnsupportedOperationException();
    }


    @Override
    String toString();

    interface SqlUnaryOperator extends Operator {

    }

    interface SqlUnaryExpOperator extends SqlUnaryOperator {

    }

    interface SqlUnaryBooleanOperator extends SqlUnaryOperator {

    }


    interface SqlDualOperator extends Operator {


    }


    interface SqlDualBooleanOperator extends SqlDualOperator {


    }

    interface SqlDualExpressionOperator extends SqlDualOperator {


        int precedence();

    }


}
