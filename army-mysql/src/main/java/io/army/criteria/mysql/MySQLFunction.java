package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.criteria.standard.SQLFunction;
import io.army.sqltype.MySQLTypes;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLFunction extends SQLFunction {


    interface _GroupConcatSeparatorClause extends _AliasExpression<Selection> {

        Expression separator(String strVal);

        Expression separator(Supplier<String> supplier);

        Expression ifSeparator(Supplier<String> supplier);


    }


    interface _GroupConcatOrderBySpec extends _AliasExpression<Selection>
            , Statement._StaticOrderByClause<_GroupConcatSeparatorClause>
            , Statement._DynamicOrderByClause<SortItems, _GroupConcatSeparatorClause>
            , _GroupConcatSeparatorClause {

    }


    interface _OnErrorClause extends Item {

        Item onError();
    }


    interface _OnEmptyClause extends Item {

        Item onEmpty();
    }


    interface _OnEmptyOrErrorActionClause extends Item {

        Item nullWord();

        Item error();

        Item defaultValue(Expression value);

        <T> Item defaultValue(Function<T, Expression> valueOperator, T value);

        Item defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        Item defaultValue(Supplier<Expression> supplier);

    }


    interface _JsonValueOnErrorClause<I extends Item> extends _OnErrorClause {

        Statement._RightParenClause<I> onError();

    }


    interface _JsonValueOptionClause<I extends Item> extends Statement._RightParenClause<I>, _OnEmptyOrErrorActionClause {

        @Override
        _JsonValueOnErrorClause<I> nullWord();

        @Override
        _JsonValueOnErrorClause<I> error();

        @Override
        _JsonValueOnErrorClause<I> defaultValue(Expression value);

        @Override
        <T> _JsonValueOnErrorClause<I> defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonValueOnErrorClause<I> defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonValueOnErrorClause<I> defaultValue(Supplier<Expression> supplier);


    }

    interface _JsonValueOptionSpec<I extends Item> extends _JsonValueOptionClause<I> {

    }


    interface _JsonValueOnEmptySpec<I extends Item> extends _JsonValueOnErrorClause<I>, _OnEmptyClause {

        @Override
        _JsonValueOptionSpec<I> onEmpty();

    }


    interface _JsonValueOptionOnEmptySpec<I extends Item> extends _JsonValueOptionSpec<I> {


        @Override
        _JsonValueOnEmptySpec<I> nullWord();

        @Override
        _JsonValueOnEmptySpec<I> error();

        @Override
        _JsonValueOnEmptySpec<I> defaultValue(Expression value);


        @Override
        <T> _JsonValueOnEmptySpec<I> defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonValueOnEmptySpec<I> defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonValueOnEmptySpec<I> defaultValue(Supplier<Expression> supplier);

    }


    interface _JsonValueReturningSpec<I extends Expression> extends _JsonValueOptionOnEmptySpec<I> {

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type);

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type, Expression n);

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type, int n);

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type, Expression n, SQLElement charset);

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type, Expression m, Expression d);

        _JsonValueOptionOnEmptySpec<I> returning(MySQLCastType type, int m, int d);

    }


    interface _JsonValueLeftParenClause<I extends Expression> {

        _JsonValueReturningSpec<I> leftParen(Expression jsonDoc, Expression path);

        _JsonValueReturningSpec<I> leftParen(Expression jsonDoc, String path);

        _JsonValueReturningSpec<I> leftParen(String jsonDoc, String path);

    }



    /*-------------------below Json table api -------------------*/


    interface _JsonTableColumnCommaSpec<R extends Item> extends Statement._RightParenClause<R> {

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLs.WordsForOrdinality forOrdinality);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, Expression n, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, int n, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, Expression n, SQLElement charset, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, int p, int m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLTypes type, Expression p, Expression m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        /*-------------------below exists path -------------------*/


        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, Expression n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, int n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, Expression n, SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, int p, int m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLTypes type, Expression p, Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);


        /*-------------------below nested -------------------*/

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> commaNested(Function<String, Expression> operator, String path);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> commaNestedPath(Function<String, Expression> operator, String path);

    }


    interface _JsonTableOnErrorClause<I extends Item> extends _OnErrorClause {

        @Override
        _JsonTableColumnCommaSpec<I> onError();

    }


    interface _JsonTableOnErrorActionSpec<I extends Item> extends _OnEmptyOrErrorActionClause
            , _JsonTableColumnCommaSpec<I> {

        @Override
        _JsonTableOnErrorClause<I> nullWord();

        @Override
        _JsonTableOnErrorClause<I> error();

        @Override
        _JsonTableOnErrorClause<I> defaultValue(Expression value);

        @Override
        <T> _JsonTableOnErrorClause<I> defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonTableOnErrorClause<I> defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonTableOnErrorClause<I> defaultValue(Supplier<Expression> supplier);
    }


    interface _JsonTableOnEmptySpec<I extends Item> extends _JsonTableOnErrorClause<I>, _OnEmptyClause {

        @Override
        _JsonTableOnErrorActionSpec<I> onEmpty();

    }


    interface _JsonTableOnEmptyActionSpec<I extends Item> extends _JsonTableOnErrorActionSpec<I> {

        @Override
        _JsonTableOnEmptySpec<I> nullWord();

        @Override
        _JsonTableOnEmptySpec<I> error();

        @Override
        _JsonTableOnEmptySpec<I> defaultValue(Expression value);

        @Override
        <T> _JsonTableOnEmptySpec<I> defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonTableOnEmptySpec<I> defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonTableOnEmptySpec<I> defaultValue(Supplier<Expression> supplier);

    }


    interface _JsonTableColumnLeftParenClause<R extends Item> {

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLs.WordsForOrdinality forOrdinality);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, Expression n, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, int n, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, Expression n, SQLElement charset, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, int p, int m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLTypes type, Expression p, Expression m, MySQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        /*-------------------below exists path -------------------*/


        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, Expression n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, int n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, Expression n, SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, int p, int m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLTypes type, Expression p, Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);


        /*-------------------below nested -------------------*/

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> leftParenNested(Function<String, Expression> operator, String path);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> leftParenNestedPath(Function<String, Expression> operator, String path);

    }

    interface _JsonTableColumnsClause<R extends Item> {

        _JsonTableColumnLeftParenClause<R> columns();

    }

    interface _JsonTableLeftParenClause<I extends Item> {

        _JsonTableColumnsClause<I> leftParen(Expression expr, Expression path);

        _JsonTableColumnsClause<I> leftParen(Expression expr, Function<String, Expression> valueOperator, String path);

        /**
         * @param expr wrap to parameter expression
         */
        _JsonTableColumnsClause<I> leftParen(String expr, Function<String, Expression> valueOperator, String path);

    }


}
