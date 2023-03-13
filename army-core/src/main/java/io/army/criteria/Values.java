package io.army.criteria;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * This interface representing VALUES statement
 * </p>
 *
 * @since 1.0
 */
public interface Values extends DqlStatement, DialectStatement, ValuesQuery {


    interface _StaticValueRowCommaDualSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Expression exp);

        _StaticValueRowCommaDualSpec<RR> comma(Expression exp1, Expression exp2);


        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value);

        _StaticValueRowCommaDualSpec<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2);

    }

    interface _StaticValueRowCommaQuadraSpec<RR> extends Statement._RightParenClause<RR> {

        Statement._RightParenClause<RR> comma(Expression exp);

        Statement._RightParenClause<RR> comma(Expression exp1, Expression exp2);

        Statement._RightParenClause<RR> comma(Expression exp1, Expression exp2, Expression exp3);

        _StaticValueRowCommaQuadraSpec<RR> comma(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2);

        Statement._RightParenClause<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> comma(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);

    }


    interface _StaticValueLeftParenClause<RR> {

        Statement._RightParenClause<RR> leftParen(Expression exp);

        _StaticValueRowCommaDualSpec<RR> leftParen(Expression exp1, Expression exp2);

        Statement._RightParenClause<RR> leftParen(Expression exp1, Expression exp2, Expression exp3);

        _StaticValueRowCommaQuadraSpec<RR> leftParen(Expression exp1, Expression exp2, Expression exp3, Expression exp4);

        Statement._RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value);

        _StaticValueRowCommaDualSpec<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2);

        Statement._RightParenClause<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3);

        _StaticValueRowCommaQuadraSpec<RR> leftParen(Function<Object, Expression> valueOperator, Object value1, Object value2, Object value3, Object value4);

    }


    interface _DynamicValuesClause<VR> {

        VR values(Consumer<RowConstructor> consumer);

    }

    interface _StaticValuesClause<VR> extends Item {

        VR values();
    }


}
