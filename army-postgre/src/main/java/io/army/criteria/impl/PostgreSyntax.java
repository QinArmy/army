package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.dialect._Constant;
import io.army.meta.FieldMeta;
import io.army.util._StringUtils;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSyntax extends PostgreFuncSyntax {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SQLSyntax.ArgDistinct {

    }

    public interface WordMaterialized extends SQLWords {

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


    private enum KeyWordMaterialized implements WordMaterialized {

        MATERIALIZED(" MATERIALIZED"),
        NOT_MATERIALIZED(" NOT MATERIALIZED");

        private final String spaceWord;

        KeyWordMaterialized(String spaceWord) {
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


    }//KeyWordMaterialized


    public static final Modifier ALL = SelectModifier.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordMaterialized MATERIALIZED = KeyWordMaterialized.MATERIALIZED;

    public static final WordMaterialized NOT_MATERIALIZED = KeyWordMaterialized.NOT_MATERIALIZED;


    public static Expression excluded(FieldMeta<?> field) {
        return ContextStack.peek().insertValueField(field, PostgreExcludedField::excludedField);
    }

    /*-------------------below private method -------------------*/


    private static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.POINT)
                .append(keyWordEnum.name())
                .toString();
    }


}
