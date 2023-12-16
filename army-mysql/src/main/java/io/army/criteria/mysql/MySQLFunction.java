package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.standard.SQLFunction;
import io.army.sqltype.MySQLType;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLFunction extends SQLFunction {


    interface _GroupConcatSeparatorClause extends Clause {

        Clause separator(String strVal);

        Clause separator(Supplier<String> supplier);

        Clause ifSeparator(Supplier<String> supplier);


    }


    interface _GroupConcatOrderBySpec extends Statement._StaticOrderByClause<_GroupConcatSeparatorClause>
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


    interface _JsonValueOnErrorClause extends _OnErrorClause {


    }


    interface _JsonValueOptionClause extends _OnEmptyOrErrorActionClause {

        @Override
        _JsonValueOnErrorClause nullWord();

        @Override
        _JsonValueOnErrorClause error();

        @Override
        _JsonValueOnErrorClause defaultValue(Expression value);

        @Override
        <T> _JsonValueOnErrorClause defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonValueOnErrorClause defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonValueOnErrorClause defaultValue(Supplier<Expression> supplier);


    }

    interface _JsonValueOptionSpec extends _JsonValueOptionClause {

    }


    interface _JsonValueOnEmptySpec extends _JsonValueOnErrorClause, _OnEmptyClause {

        @Override
        _JsonValueOptionSpec onEmpty();

    }


    interface _JsonValueOptionOnEmptySpec extends _JsonValueOptionSpec {


        @Override
        _JsonValueOnEmptySpec nullWord();

        @Override
        _JsonValueOnEmptySpec error();

        @Override
        _JsonValueOnEmptySpec defaultValue(Expression value);


        @Override
        <T> _JsonValueOnEmptySpec defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonValueOnEmptySpec defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonValueOnEmptySpec defaultValue(Supplier<Expression> supplier);

    }


    interface _JsonValueReturningSpec extends _JsonValueOptionOnEmptySpec {

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, int n);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n, SQLElement charset);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression m, Expression d);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, int m, int d);

    }





    /*-------------------below Json table api -------------------*/


    interface _JsonTableColumnCommaSpec<R extends Item> extends Statement._RightParenClause<R> {

        _JsonTableOnEmptyActionSpec<R> comma(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, Expression n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, int n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, Expression n, SQLElement charset, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, int p, int m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> comma(String name, MySQLType type, Expression p, Expression m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        /*-------------------below exists path -------------------*/


        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, Expression n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, int n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, Expression n, SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, int p, int m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> comma(String name, MySQLType type, Expression p, Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);


        /*-------------------below nested -------------------*/

        _JsonTableColumnCommaSpec<R> commaNested(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> commaNested(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> commaNestedPath(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> commaNestedPath(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

    }


    interface _JsonTableOnErrorClause0<I extends Item> extends _OnErrorClause {

        @Override
        _JsonTableColumnCommaSpec<I> onError();

    }


    interface _JsonTableOnErrorActionSpec<I extends Item> extends _OnEmptyOrErrorActionClause
            , _JsonTableColumnCommaSpec<I> {

        @Override
        _JsonTableOnErrorClause0<I> nullWord();

        @Override
        _JsonTableOnErrorClause0<I> error();

        @Override
        _JsonTableOnErrorClause0<I> defaultValue(Expression value);

        @Override
        <T> _JsonTableOnErrorClause0<I> defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonTableOnErrorClause0<I> defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonTableOnErrorClause0<I> defaultValue(Supplier<Expression> supplier);
    }


    interface _JsonTableOnEmptySpec<I extends Item> extends _JsonTableOnErrorClause0<I>, _OnEmptyClause {

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

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, Expression n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, int n, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, Expression n, SQLElement charset, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, int p, int m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableOnEmptyActionSpec<R> leftParen(String name, MySQLType type, Expression p, Expression m, SQLs.WordPath path, Function<String, Expression> operator, String stringPath);

        /*-------------------below exists path -------------------*/


        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, Expression n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, int n, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, Expression n, SQLElement charset, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, int n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, Expression n, SQLElement charset, SQLIdentifier collate, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, int p, int m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);

        _JsonTableColumnCommaSpec<R> leftParen(String name, MySQLType type, Expression p, Expression m, MySQLs.WordExistsPath path, Function<String, Expression> operator, String stringPath);


        /*-------------------below nested -------------------*/

        _JsonTableColumnCommaSpec<R> leftParenNested(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> leftParenNested(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> leftParenNestedPath(Expression path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

        _JsonTableColumnCommaSpec<R> leftParenNestedPath(Function<String, Expression> operator, String path, Function<MySQLJsonNestedClause, MySQLJsonColumns> function);

    }

    interface _JsonTableColumnsClause<R extends Item> {

        _JsonTableColumnLeftParenClause<R> columns();

        R columns(Consumer<MySQLJsonColumnClause> consumer);

    }

    interface _JsonTableLeftParenClause<I extends Item> {

        _JsonTableColumnsClause<I> leftParen(Expression expr, Expression path);

        _JsonTableColumnsClause<I> leftParen(Expression expr, Function<String, Expression> valueOperator, String path);

        /**
         * @param expr wrap to parameter expression
         */
        _JsonTableColumnsClause<I> leftParen(String expr, Function<String, Expression> valueOperator, String path);

    }


    interface _JsonTableDynamicOnErrorClause extends _OnErrorClause {

        @Override
        MySQLJsonColumnClause onError();

    }


    interface _JsonTableDynamicOnErrorActionSpec extends _OnEmptyOrErrorActionClause
            , MySQLJsonColumnClause {

        @Override
        _JsonTableDynamicOnErrorClause nullWord();

        @Override
        _JsonTableDynamicOnErrorClause error();

        @Override
        _JsonTableDynamicOnErrorClause defaultValue(Expression value);

        @Override
        <T> _JsonTableDynamicOnErrorClause defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonTableDynamicOnErrorClause defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonTableDynamicOnErrorClause defaultValue(Supplier<Expression> supplier);
    }


    interface _JsonTableDynamicOnEmptySpec extends _JsonTableDynamicOnErrorClause, _OnEmptyClause {

        @Override
        _JsonTableDynamicOnErrorActionSpec onEmpty();

    }


    interface _JsonTableDynamicOnEmptyActionSpec extends _JsonTableDynamicOnErrorActionSpec {

        @Override
        _JsonTableDynamicOnEmptySpec nullWord();

        @Override
        _JsonTableDynamicOnEmptySpec error();

        @Override
        _JsonTableDynamicOnEmptySpec defaultValue(Expression value);

        @Override
        <T> _JsonTableDynamicOnEmptySpec defaultValue(Function<T, Expression> valueOperator, T value);

        @Override
        _JsonTableDynamicOnEmptySpec defaultValue(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        _JsonTableDynamicOnEmptySpec defaultValue(Supplier<Expression> supplier);

    }


    interface _JsonTableOnErrorClause {

        Object onError();

    }


    interface _JsonTableEventHandleClause {

        _JsonTableOnErrorClause spaceNull();

        _JsonTableOnErrorClause spaceDefault(Object jsonExp);

        _JsonTableOnErrorClause spaceError();

    }

    interface _JsonTableOnEmptyClause extends _JsonTableOnErrorClause {

        _JsonTableEventHandleClause onEmpty();

    }


    interface _JsonTableEmptyHandleClause extends _JsonTableEventHandleClause {


        @Override
        _JsonTableOnEmptyClause spaceNull();

        @Override
        _JsonTableOnEmptyClause spaceDefault(Object jsonExp);

        @Override
        _JsonTableOnEmptyClause spaceError();


    }


    interface _JsonTableColumnCommaClause {

        /**
         * @param name          column name
         * @param forOrdinality see {@link MySQLs#FOR_ORDINALITY}
         */
        _JsonTableColumnCommaClause comma(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_JsonTableEmptyHandleClause> consumer);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

    }


    interface _JsonTableColumnSpaceClause {

        _JsonTableColumnCommaClause space(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_JsonTableEmptyHandleClause> consumer);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

    }

    interface _JsonTableColumnConsumerClause {

        _JsonTableColumnConsumerClause accept(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_JsonTableEmptyHandleClause> consumer);

        _JsonTableColumnConsumerClause accept(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnConsumerClause accept(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnConsumerClause accept(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

    }


}
