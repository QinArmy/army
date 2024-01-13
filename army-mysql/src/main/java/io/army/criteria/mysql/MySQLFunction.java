/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.mysql;

import io.army.criteria.Clause;
import io.army.criteria.Statement;
import io.army.criteria.TypeItem;
import io.army.criteria.impl.SQLs;
import io.army.criteria.standard.SQLFunction;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MySQLFunction extends SQLFunction {


    interface _GroupConcatSeparatorClause extends Clause {

        Clause separator(String strVal);

        Clause separator(Supplier<String> supplier);

        Clause ifSeparator(Supplier<String> supplier);


    }


    interface _GroupConcatOrderBySpec extends Statement._StaticOrderByClause<_GroupConcatSeparatorClause>,
            Statement._DynamicOrderByClause<_GroupConcatSeparatorClause>,
            _GroupConcatSeparatorClause {

    }



    interface _ValueOnErrorClause {

        Object onError();

    }


    interface _ValueErrorActionClause {

        _ValueOnErrorClause spaceNull();

        _ValueOnErrorClause spaceDefault(Object jsonExp);

        _ValueOnErrorClause spaceError();

    }

    interface _ValueOnEmptySpec extends _ValueOnErrorClause {

        _ValueErrorActionClause onEmpty();

    }


    interface _ValueEmptyActionSpec extends _ValueErrorActionClause {


        @Override
        _ValueOnEmptySpec spaceNull();

        @Override
        _ValueOnEmptySpec spaceDefault(Object jsonExp);

        @Override
        _ValueOnEmptySpec spaceError();


    }


    interface _JsonValueReturningSpec extends _ValueEmptyActionSpec {

        _ValueEmptyActionSpec returning(MySQLCastType type);

        _ValueEmptyActionSpec returning(MySQLCastType type, Object length);

        _ValueEmptyActionSpec returning(MySQLCastType type, Object precision, Object scale);

        _ValueEmptyActionSpec returning(MySQLCastType type, SQLs.WordsCharacterSet charSet, String charsetName);

        _ValueEmptyActionSpec returning(MySQLCastType type, SQLs.WordsCharacterSet charSet, String charsetName, SQLs.WordCollate collate, String collation);

        _ValueEmptyActionSpec returning(MySQLCastType type, Object length, SQLs.WordsCharacterSet charSet, String charsetName);

        _ValueEmptyActionSpec returning(MySQLCastType type, Object length, SQLs.WordsCharacterSet charSet, String charsetName, SQLs.WordCollate collate, String collation);


    }


    interface _JsonTableColumnCommaClause {

        /**
         * @param name          column name
         * @param forOrdinality see {@link SQLs#FOR_ORDINALITY}
         */
        _JsonTableColumnCommaClause comma(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_ValueEmptyActionSpec> consumer);

        _JsonTableColumnCommaClause comma(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause comma(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

    }


    interface _JsonTableColumnSpaceClause {

        _JsonTableColumnCommaClause space(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_ValueEmptyActionSpec> consumer);

        _JsonTableColumnCommaClause space(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause space(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);


    }

    interface _JsonTableColumnConsumerClause {

        _JsonTableColumnConsumerClause column(String name, SQLs.WordsForOrdinality forOrdinality);

        _JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordPath path, Object pathExp, Consumer<_ValueEmptyActionSpec> consumer);

        _JsonTableColumnConsumerClause column(String name, TypeItem type, SQLs.WordExists exists, SQLs.WordPath path, Object pathExp);

        _JsonTableColumnConsumerClause column(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnConsumerClause column(SQLs.WordNested nested, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);

        _JsonTableColumnCommaClause column(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, Consumer<_JsonTableColumnSpaceClause> consumer);

        _JsonTableColumnCommaClause column(SQLs.WordNested nested, SQLs.WordPath path, Object pathExp, SQLs.WordColumns columns, SQLs.SymbolSpace space, Consumer<_JsonTableColumnConsumerClause> consumer);


    }


}
