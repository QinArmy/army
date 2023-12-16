package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.standard.SQLFunction;

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
