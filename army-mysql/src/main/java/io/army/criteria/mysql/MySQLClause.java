package io.army.criteria.mysql;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.SortItems;
import io.army.criteria.Statement;
import io.army.criteria.impl._AliasExpression;
import io.army.sqltype.MySQLTypes;

import java.util.function.Supplier;

public interface MySQLClause {


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


    interface _JsonValueOnErrorClause {

        Expression onError();

    }


    interface _JsonValueOptionClause {

        _JsonValueOnErrorClause nullWord();

        _JsonValueOnErrorClause error();

        _JsonValueOnErrorClause defaultValue(Expression value);

        _JsonValueOnErrorClause defaultValue(Supplier<? extends Expression> supplier);


    }

    interface _JsonValueOptionSpec extends _JsonValueOptionClause, Expression {

    }


    interface _JsonValueOnEmptySpec extends _JsonValueOnErrorClause {

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
        _JsonValueOnEmptySpec defaultValue(Supplier<? extends Expression> supplier);

    }


    interface _JsonValueReturningSpec extends _JsonValueOptionOnEmptySpec {

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression n, MySQLCharset charset);

        _JsonValueOptionOnEmptySpec returning(MySQLCastType type, Expression m, Expression d);

    }


    interface _ForOrdinalityClause<R> {

        _JsonTableColumnCommaSpec<R> forOrdinality();
    }


    interface _JsonTableColumnPathClause<R> {

        _JsonTableOnEmptyOptionClause<R> path(String stringPath);

        _JsonTableColumnCommaSpec<R> existsPath(String stringPath);

    }

    interface _JsonTableColumnsClause<R> {

        _JsonTableColumnLeftParenClause<R> columns();

    }

    interface _JsonTableColumnLeftParenClause<R> {

        _ForOrdinalityClause<R> leftParen(String name);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, MySQLCharset charset);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, MySQLCharset charset, String collate);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long n, String charset, String collate);

        _JsonTableColumnPathClause<R> leftParen(String name, MySQLTypes type, long p, int m);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> leftParenNested(String path);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> leftParenNestedPath(String path);

    }


    interface _JsonTableColumnCommaSpec<R> extends Statement._RightParenClause<R> {

        _ForOrdinalityClause<R> comma(String name);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, MySQLCharset charset);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, MySQLCharset charset, String collate);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long n, String charset, String collate);

        _JsonTableColumnPathClause<R> comma(String name, MySQLTypes type, long p, int m);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> commaNested(String path);

        _JsonTableColumnsClause<_JsonTableColumnCommaSpec<R>> commaNestedPath(String path);

    }

    interface _JsonTableOnErrorClause<R> {

        _JsonTableColumnCommaSpec<R> onError();

    }

    interface _JsonTableColumnOnErrorOptionClause<R> extends _JsonTableColumnCommaSpec<R> {

        _JsonTableOnErrorClause<R> nullWord();

        _JsonTableOnErrorClause<R> error();

        _JsonTableOnErrorClause<R> defaultValue(String jsonString);


    }

    interface _JsonTableOnEmptySpec<R> extends _JsonTableOnErrorClause<R> {

        _JsonTableColumnOnErrorOptionClause<R> onEmpty();

    }

    interface _JsonTableOnEmptyOptionClause<R> extends _JsonTableColumnOnErrorOptionClause<R> {


        @Override
        _JsonTableOnEmptySpec<R> nullWord();

        @Override
        _JsonTableOnEmptySpec<R> error();

        @Override
        _JsonTableOnEmptySpec<R> defaultValue(String jsonString);


    }


}
