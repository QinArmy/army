package io.army.criteria.mysql;

import io.army.criteria.Clause;
import io.army.criteria.Expression;
import io.army.criteria.Statement;

import java.util.function.Supplier;

public interface MySQLClause {


    interface _GroupConcatSeparatorClause extends Clause {

        Clause separator(String strVal);

        Clause separator(Supplier<String> supplier);

        Clause ifSeparator(Supplier<String> supplier);


    }

    interface _GroupConcatOrderBySpec extends Statement._OrderByClause<Void, _GroupConcatSeparatorClause>
            , _GroupConcatSeparatorClause {

    }


    interface _JsonValueOnErrorClause {

        Expression onError();

    }


    interface _JsonValueOptionClause {

        _JsonValueOnErrorClause nullValue();

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
        _JsonValueOnEmptySpec nullValue();

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

    
}
