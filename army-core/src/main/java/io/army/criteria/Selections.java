package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.function.*;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Selections extends Item {

    //below one argument method

    Selections selection(NamedExpression exp);


    _AliasExpression<Selections> selection(Supplier<Expression> supplier);


    <R extends Item> R selection(SqlFunction<_AliasExpression<Selections>, Selections, R> function);

    //below two argument method

    Selections selection(NamedExpression exp1, NamedExpression exp2);

    <T> _AliasExpression<Selections> selection(Function<T, Expression> operator, Supplier<T> supplier);

    _AliasExpression<Selections> selection(Function<Expression, Expression> operator, Expression exp);

    _AliasExpression<Selections> selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                                           BiFunction<DataField, String, Expression> namedOperator);

    <R extends Item> R selection(SqlOneFunction<_AliasExpression<Selections>, Selections, R> function, Expression exp);

    <R extends Item> R selection(SqlOneFunction<_AliasExpression<Selections>, Selections, R> function, Supplier<Expression> supplier);

    //below three argument method

    Selections selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3);

    Selections selection(Expression exp, SQLs.WordAs as, String alias);

    Selections selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

    Selections selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    Selections selection(Supplier<Expression> supplier, SQLs.WordAs as, String alias);

    <T> _AliasExpression<Selections> selection(ExpressionOperator<Expression, T, Expression> expOperator,
                                               BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    <R extends Item> R selection(SqlTwoFunction<_AliasExpression<Selections>, Selections, R> function, Expression exp1,
                                 Expression exp2);

    //below four argument method

    Selections selection(NamedExpression exp1, NamedExpression exp2, NamedExpression exp3, NamedExpression exp4);

    <T> Selections selection(Function<T, Expression> operator, Supplier<T> supplier, SQLs.WordAs as, String alias);

    Selections selection(Function<Expression, Expression> operator, Expression exp, SQLs.WordAs as, String alias);

    Selections selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
                         BiFunction<DataField, String, Expression> namedOperator,
                         SQLs.WordAs as, String alias);

    _AliasExpression<Selections> selection(ExpressionOperator<Expression, Object, Expression> expOperator,
                                           BiFunction<Expression, Object, Expression> operator,
                                           Function<String, ?> function, String keyName);


    <R extends Item> R selection(SqlThreeFunction<_AliasExpression<Selections>, Selections, R> function, Expression exp1,
                                 Expression exp2, Expression exp3);


    //below five argument method


    //below six argument method

    Selections selection(NamedExpression exp1, SQLs.WordAs as1, String alias1,
                         NamedExpression exp2, SQLs.WordAs as2, String alias2);

    <P> Selections selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent,
                             String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);


    Selections selection(Supplier<Expression> function1, SQLs.WordAs as1, String alias1,
                         Supplier<Expression> function2, SQLs.WordAs as2, String alias2);

    Selections selection(ExpressionOperator<Expression, Object, Expression> expOperator,
                         BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                         String keyName, SQLs.WordAs as, String alias);


}
