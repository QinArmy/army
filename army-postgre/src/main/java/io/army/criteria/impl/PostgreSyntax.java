package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Query;
import io.army.criteria.SQLWords;
import io.army.dialect._Constant;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.util._StringUtils;

import java.util.function.BiFunction;

/**
 * <p>
 * Package class
 * </p>
 *
 * @since 1.0
 */
abstract class PostgreSyntax extends PostgreMiscellaneousFunctions {

    /**
     * Package constructor
     */
    PostgreSyntax() {
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SqlSyntax.ArgDistinct {

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

    private enum FromNormalizedWord implements SQLsSyntax.BooleanTestWord {
        FROM_NORMALIZED(" FROM NORMALIZED"),
        NORMALIZED(" NORMALIZED");

        private final String spaceWords;

        FromNormalizedWord(String spaceWords) {
            this.spaceWords = spaceWords;
        }

        @Override
        public final String render() {
            return this.spaceWords;
        }


        @Override
        public String toString() {
            return keyWordToString(this);
        }


    }//FromNormalizedWord


    public static final Modifier ALL = SelectModifier.ALL;

    public static final WordDistinct DISTINCT = KeyWordDistinct.DISTINCT;

    public static final WordMaterialized MATERIALIZED = KeyWordMaterialized.MATERIALIZED;

    public static final WordMaterialized NOT_MATERIALIZED = KeyWordMaterialized.NOT_MATERIALIZED;

    public static final SQLsSyntax.BooleanTestWord FROM_NORMALIZED = FromNormalizedWord.FROM_NORMALIZED;

    public static final SQLsSyntax.BooleanTestWord NORMALIZED = FromNormalizedWord.NORMALIZED;


    public static Expression excluded(FieldMeta<?> field) {
        return ContextStack.peek().insertValueField(field, PostgreExcludedField::excludedField);
    }


    /**
     * <p>
     * The {@link MappingType} of operator return type: the {@link  MappingType} of leftText.
     * </p>
     *
     * @param leftText not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see Expression#apply(BiFunction, BiFunction, Object)
     * @see Postgres#startsWith(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-OTHER">text ^@ text → boolean</a>
     */
    public static Expression caretAt(Expression leftText, Expression rightText) {
        return Expressions.dualExp((OperationExpression) leftText, DualOperator.CARET_AT, rightText);
    }

    /**
     * <p>
     * The {@link MappingType} of operator return type: the {@link  MappingType} of leftText.
     * </p>
     *
     * @param left  not {@link SQLs#DEFAULT} etc.
     * @param right not {@link SQLs#DEFAULT} etc.
     * @see Expression#apply(BiFunction, Expression)
     * @see Expression#apply(BiFunction, BiFunction, Object)
     * @see <a href="https://www.postgresql.org/docs/current/functions-bitstring.html#FUNCTIONS-BIT-STRING-OP-TABLE">bit || bit → bit</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-string.html#FUNCTIONS-STRING-SQL">text || text → text <br/>
     * text || anynonarray → text <br/>
     * anynonarray || text → text
     * </a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-binarystring.html#FUNCTIONS-BINARYSTRING-SQL">bytea || bytea → bytea</a>
     */
    public static Expression doubleVertical(Expression left, Expression right) {
        return Expressions.dualExp((OperationExpression) left, DualOperator.DOUBLE_VERTICAL, right);
    }


    /*-------------------below operator method -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of exp
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-math.html#FUNCTIONS-MATH-OP-TABLE">Absolute value operator</a>
     */
    public static Expression at(Expression exp) {
        return Expressions.unaryExp(exp, UnaryOperator.AT);
    }


    /*-------------------below private method -------------------*/


    static String keyWordToString(Enum<?> keyWordEnum) {
        return _StringUtils.builder()
                .append(Postgres.class.getSimpleName())
                .append(_Constant.POINT)
                .append(keyWordEnum.name())
                .toString();
    }


}
