package io.army.criteria;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * <p>
 * This interface representing VALUES statement
 * </p>
 *
 * @since 1.0
 */
public interface Values extends DqlStatement, DialectStatement, RowSet.DqlValues {

    interface _ValuesSpec<U extends DqlValues> {
        U asValues();
    }


    interface _StaticValueRowCommaDualSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Object value);

        _StaticValueRowCommaDualSpec<RR> comma(Object value1, Object value2);

        Statement._RightParenClause<RR> commaLiteral(Object value);

        _StaticValueRowCommaDualSpec<RR> commaLiteral(Object value1, Object value2);

    }

    interface _StaticValueRowCommaQuadraSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Object value);

        Statement._RightParenClause<RR> comma(Object value1, Object value2);

        Statement._RightParenClause<RR> comma(Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> comma(Object value1, Object value2, Object value3, Object value4);

        Statement._RightParenClause<RR> commaLiteral(Object value);

        Statement._RightParenClause<RR> commaLiteral(Object value1, Object value2);

        Statement._RightParenClause<RR> commaLiteral(Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> commaLiteral(Object value1, Object value2, Object value3, Object value4);

    }


    interface _StaticValueLeftParenClause<RR> {

        Statement._RightParenClause<RR> leftParen(Object value);

        _StaticValueRowCommaDualSpec<RR> leftParen(Object value1, Object value2);

        _StaticValueRowCommaQuadraSpec<RR> leftParen(Object value1, Object value2, Object value3, Object value4);

        Statement._RightParenClause<RR> leftParenLiteral(Object value);

        _StaticValueRowCommaDualSpec<RR> leftParenLiteral(Object value1, Object value2);

        _StaticValueRowCommaQuadraSpec<RR> leftParenLiteral(Object value1, Object value2, Object value3, Object value4);

    }


    interface _ValuesDynamicClause<C, VR> {

        VR values(Consumer<RowConstructor> consumer);

        VR values(BiConsumer<C, RowConstructor> consumer);

    }


}
