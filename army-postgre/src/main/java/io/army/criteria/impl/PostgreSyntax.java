package io.army.criteria.impl;


import io.army.criteria.Query;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSyntax extends Functions {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, FuncDistinct {

    }

    private enum SelectModifier implements Modifier {

        ALL(" ALL");

        private final String spaceWord;

        SelectModifier(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }


    }//SelectModifier


    private enum KeyWordDistinct implements WordDistinct {

        DISTINCT(" DISTINCT");

        private final String spaceWord;

        KeyWordDistinct(String spaceWord) {
            this.spaceWord = spaceWord;
        }


        @Override
        public final String render() {
            return this.spaceWord;
        }

        @Override
        public final String toString() {
            return keyWordToString(this);
        }
    }//KeyWordDistinct


    public static final Modifier ALL = SelectModifier.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;



    /*-------------------below private method -------------------*/


    private static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.POINT)
                .append(keyWordEnum.name())
                .toString();
    }


}
