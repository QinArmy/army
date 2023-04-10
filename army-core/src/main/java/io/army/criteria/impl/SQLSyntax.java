package io.army.criteria.impl;

import io.army.criteria.SQLWords;
import io.army.dialect._Constant;

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

        INTERVAL(" INTERVAL"),
        COMMA(_Constant.SPACE_COMMA),
        FROM(_Constant.SPACE_FROM),
        USING(_Constant.SPACE_USING),
        IN(" IN"),
        AS(_Constant.SPACE_AS),
        AT_TIME_ZONE(" AT TIME ZONE"),
        LEFT_PAREN(_Constant.SPACE_LEFT_PAREN),
        RIGHT_PAREN(_Constant.SPACE_RIGHT_PAREN);

        private final String spaceWords;

        FuncWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
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
