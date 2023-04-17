package io.army.criteria.impl;

import io.army.dialect.Database;

/**
 * Package interface
 */
interface Operator {

    String name();


    @Override
    String toString();

    interface SqlUnaryOperator extends Operator {

    }


    interface DualOperator extends Operator {


    }

    interface BooleanDualOperator extends DualOperator {

        String spaceRender();

        /**
         * @throws UnsupportedOperationException throw when this is {@link io.army.criteria.impl.BooleanDualOperator} type.
         */
        Database database();

    }


}
