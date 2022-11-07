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

    Query._SelectCommaSpec selection(FieldMeta<?> field);

    Query._SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2);

    Query._SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3);

    Query._SelectCommaSpec selection(FieldMeta<?> field1, FieldMeta<?> field2, FieldMeta<?> field3, FieldMeta<?> field4);

    Query._SelectCommaSpec selection(Expression exp, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(Expression exp1, SQLs.WordAs as1, String alias1, Expression exp2, SQLs.WordAs as2, String alias2);

    Query._SelectCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias);

    Query._SelectCommaSpec selection(String derivedAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(String derivedAlias1, SQLs.SymbolPeriod period1, String fieldAlias1, String derivedAlias2, SQLs.SymbolPeriod period2, String fieldAlias2);

    Query._SelectCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, TableMeta<?> table);

    <P> Query._SelectCommaSpec selection(String parenAlias, SQLs.SymbolPeriod period1, ParentTableMeta<P> parent, String childAlias, SQLs.SymbolPeriod period2, ComplexTableMeta<P, ?> child);

    Query._SelectCommaSpec selection(String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field);

    Query._SelectCommaSpec selection(String tableAlias1, SQLs.SymbolPeriod period1, FieldMeta<?> field1, String tableAlias2, SQLs.SymbolPeriod period2, FieldMeta<?> field2);

    Statement._AsClause<Query._SelectCommaSpec> selection(Supplier<Expression> supplier);

    Query._SelectCommaSpec selection(Supplier<Expression> funcRef, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(Function<Expression, Expression> funcRef, FieldMeta<?> field, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, FieldMeta<?> field, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(Function<Expression, Expression> funcRef, String tableAlias, SQLs.SymbolPeriod period, String fieldAlias, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(Supplier<Expression> funcRef1, SQLs.WordAs as1, String alias1, Supplier<Expression> funcRef2, SQLs.WordAs as2, String alias2);

    <I extends Item> I selection(Function<Function<Expression, Statement._AsClause<Query._SelectCommaSpec>>, I> sqlFunc);

    <I extends Item> I selection(BiFunction<Expression, Function<Expression, Statement._AsClause<Query._SelectCommaSpec>>, I> sqlFunc, Expression expression);

    <E extends RightOperand> Statement._AsClause<Query._SelectCommaSpec> selection(Function<E, Expression> expOperator, Supplier<E> supplier);

    Query._SelectCommaSpec selection(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator, BiFunction<DataField, String, Expression> namedOperator, SQLs.WordAs as, String alias);

    <T> Query._SelectCommaSpec selection(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, T operand, SQLs.WordAs as, String alias);

    <T> Query._SelectCommaSpec selection(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter, SQLs.WordAs as, String alias);

    Query._SelectCommaSpec selection(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName, SQLs.WordAs as, String alias);


}
