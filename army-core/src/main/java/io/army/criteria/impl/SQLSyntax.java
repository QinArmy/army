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

    enum FuncWord implements SQLWords {

        INTERVAL,
        COMMA,
        FROM,
        USING,
        IN,
        AS,
        AT_TIME_ZONE,
        LEFT_PAREN,
        RIGHT_PAREN;

        @Override
        public final String render() {
            final String words;
            switch (this) {
                case COMMA:
                    words = ",";
                    break;
                case LEFT_PAREN:
                    words = "(";
                    break;
                case RIGHT_PAREN:
                    words = ")";
                    break;
                case AT_TIME_ZONE:
                    words = "AT TIME ZONE";
                    break;
                default:
                    words = this.name();
            }
            return words;
        }

        @Override
        public final String toString() {
            return String.format("%s.%s", FuncWord.class.getSimpleName(), this.name());
        }


    }//Word


    /**
     * @see SQLs#DISTINCT
     */
    public interface ArgDistinct extends SQLWords {

    }


}
