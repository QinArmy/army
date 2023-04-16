package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SQLWords;
import io.army.criteria.SimplePredicate;

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
abstract class SqlSyntax {


    SqlSyntax() {
        throw new UnsupportedOperationException();
    }


    /**
     * @see SQLs#DISTINCT
     */
    public interface ArgDistinct extends SQLWords {

    }

    public interface WordEscape extends SQLWords {

    }


    public interface SymbolAsterisk {

    }

    public interface WordNull extends SQLsSyntax.BooleanTestWord, Expression {

    }

    public interface WordAs extends SQLWords {

    }

    public interface WordAnd {

    }


    public interface WordDefault extends Expression {

    }

    public interface WordBooleans extends SQLsSyntax.BooleanTestWord, SimplePredicate {

    }

    public interface SymbolPeriod {

    }


    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    interface ArmyKeyWord extends SQLWords {

    }


}
