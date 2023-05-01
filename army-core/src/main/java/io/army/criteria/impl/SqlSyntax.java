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

    public interface SymbolSpace {

    }

    public interface NullOption {

    }

    public interface BooleanTestWord extends SQLWords {

    }


    public interface WordNull extends BooleanTestWord, Expression, NullOption { // extends Expression not SimpleExpression

    }


    public interface WordAs extends SQLWords {

    }

    public interface WordAnd {

    }


    public interface WordDefault extends Expression {

    }

    public interface WordBooleans extends BooleanTestWord, SimplePredicate {

    }

    public interface DocumentValueOption extends SQLWords {

    }

    public interface WordDocument extends BooleanTestWord, DocumentValueOption {

    }

    public interface WordContent extends DocumentValueOption {

    }

    public interface SymbolPeriod {

    }


    /**
     * package interface,this interface only is implemented by class or enum,couldn't is extended by interface.
     */
    interface ArmyKeyWord extends SQLWords {

    }


}
