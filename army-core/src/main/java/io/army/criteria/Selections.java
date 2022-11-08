package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.ExpressionOperator;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.TableMeta;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface Selections {

    //below one argument method

    Query._SelectionsCommaSpec selection(FieldMeta<?> field);


    Statement._AsClause<Query._SelectionsCommaSpec> selection(Supplier<Expression> supplier);

    <I extends Item> I selection(Function<Function<Expression, Statement._AsClause<Query._SelectionsCommaSpec>>, I> sqlFunc);

    //below two argument method

    Query._SelectionsCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2);

    <I extends Item> I selection(BiFunction<Expression, Function<Expression, Statement._AsClause<Query._SelectionsCommaSpec>>, I> sqlFunc, Expression expression);

    <E extends RightOperand> Statement._AsClause<Query._SelectionsCommaSpec> selection(Function<E, Expression> expOperator, Supplier<E> supplier);

    //below three argument method

    Query._SelectionsCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

    Query._SelectionsCommaSpec selection(Expression exp, SQLs.WordAs as, String alias);

    Query._SelectionsCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, SQLs.SymbolStar star);

    Query._SelectionsCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

    Query._SelectionsCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    Query._SelectionsCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

    Query._SelectionsCommaSpec selection(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

    //below four argument method

    Query._SelectionsCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

    <T> Query._SelectionsCommaSpec selection(Function<T, Expression> valueOperator, T value, SQLs.WordAs as, String alias);

    Query._SelectionsCommaSpec selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

    //below five argument method

    Query._SelectionsCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

    Query._SelectionsCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

    <T> Query._SelectionsCommaSpec selection(ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);


    //below six argument method

    Query._SelectionsCommaSpec selection(FieldMeta<?> field1, SQLs.WordAs as1, String alias1
            , FieldMeta<?> field2, SQLs.WordAs as2, String alias2);

    Query._SelectionsCommaSpec selection(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1
            , String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

    <P> Query._SelectionsCommaSpec selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent
            , String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    Query._SelectionsCommaSpec selection(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1
            , String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

    Query._SelectionsCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
            , FieldMeta<?> field, SQLs.WordAs as, String alias);

    Query._SelectionsCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period
            , String fieldAlias, SQLs.WordAs as, String alias);

    Query._SelectionsCommaSpec selection(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1
            , Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

    Query._SelectionsCommaSpec selection(ExpressionOperator<Expression, Object, Expression> expOperator
            , BiFunction<Expression, Object, Expression> operator, Function<String, ?> function
            , String keyName, SQLs.WordAs as, String alias);

    <I extends Item, T> I selection(BiFunction<Expression, Function<Expression, Statement._AsClause<Query._SelectionsCommaSpec>>, I> sqlFunc
            , ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, T operand);

    <I extends Item, T> I selection(BiFunction<Expression, Function<Expression, Statement._AsClause<Query._SelectionsCommaSpec>>, I> sqlFunc
            , ExpressionOperator<Expression, T, Expression> expOperator
            , BiFunction<Expression, T, Expression> operator, Function<String, ?> function
            , String keyName);


}
