package io.army.criteria.mysql;

import io.army.criteria.Clause;
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


}
