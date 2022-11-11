package io.army.criteria.impl;

import io.army.criteria.SQLWords;

/**
 * package class,this class is base class of below
 * <ul>
 *     <li>{@link SQLsSyntax}</li>
 *     <li>{@link Functions}</li>
 *     <li>dialect syntax utils</li>
 *     <li>dialect functions utils</li>
 * </ul>
 *
 * @since 1.0
 */
abstract class SQLSyntax {

    SQLSyntax() {
        throw new UnsupportedOperationException();
    }


    /**
     * @see SQLs#DISTINCT
     */
    public interface ArgDistinct extends SQLWords {

    }


}
