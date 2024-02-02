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
import io.army.criteria.Expression;
import io.army.criteria.InsertStatement;
import io.army.criteria.Item;
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;


/**
 * <p>This interface representing MySQL LOAD DATA Statement
 * <p>More document see {@link MySQLs#loadDataStmt()}
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_local_infile">Server local_infile system variables</a>
 * @see <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html">client allowLoadLocalInfile property </a>
 */
public interface MySQLLoadData extends MySQLStatement, DmlCommand {


    interface _LoadSetSpec<I extends Item, T> extends InsertStatement._StaticAssignmentSetClause<T, _LoadSetSpec<I, T>>,
            _AsCommandClause<I> {

    }


    interface _ColumnOrVarListSpec<I extends Item, T> extends _LoadSetSpec<I, T> {

        _LoadSetSpec<I, T> parens(Consumer<Clause._VariadicExprSpaceClause> consumer);

        _LoadSetSpec<I, T> ifParens(Consumer<Clause._VariadicExprSpaceClause> consumer);

        _LoadSetSpec<I, T> parens(SQLs.SymbolSpace space, Consumer<Consumer<Expression>> consumer);

        _LoadSetSpec<I, T> ifParens(SQLs.SymbolSpace space, Consumer<Consumer<Expression>> consumer);

    }


    interface _IgnoreLineSpec<I extends Item, T> extends _ColumnOrVarListSpec<I, T> {

        _ColumnOrVarListSpec<I, T> ignore(long rowNumber, SQLs.LinesWord word);

        _ColumnOrVarListSpec<I, T> ignore(Supplier<Long> supplier, SQLs.LinesWord word);

        _ColumnOrVarListSpec<I, T> ifIgnore(Supplier<Long> supplier, SQLs.LinesWord word);
    }


    interface _EscapedByClause {

        void escapedBy(char ch);

        void escapedBy(String ch);

        void ifEscapedBy(Supplier<String> supplier);
    }


    interface _EnclosedBySpec extends _EscapedByClause, Item {

        _EscapedByClause enclosedBy(char ch);

        _EscapedByClause enclosedBy(String ch);

        _EscapedByClause ifEnclosedBy(Supplier<String> supplier);

        _EscapedByClause optionallyEnclosedBy(char ch);

        _EscapedByClause optionallyEnclosedBy(String ch);

        _EscapedByClause ifOptionallyEnclosedBy(Supplier<String> supplier);

    }


    interface _TerminatedByClause {

        Item terminatedBy(String string);

        Item terminatedBy(Supplier<String> supplier);

        Item ifTerminatedBy(Supplier<String> supplier);

    }

    interface _ColumnTerminatedBySpec extends _TerminatedByClause, _EnclosedBySpec {

        @Override
        _EnclosedBySpec terminatedBy(String string);

        @Override
        _EnclosedBySpec terminatedBy(Supplier<String> supplier);

        @Override
        _EnclosedBySpec ifTerminatedBy(Supplier<String> supplier);

    }

    interface _StartingBySpec extends _TerminatedByClause {

        _TerminatedByClause startingBy(String string);

        _TerminatedByClause ifStartingBy(Supplier<String> supplier);

    }


    interface _LinesSpec<I extends Item, T> extends _IgnoreLineSpec<I, T> {

        _IgnoreLineSpec<I, T> lines(Consumer<_StartingBySpec> consumer);

        _IgnoreLineSpec<I, T> ifLines(Consumer<_StartingBySpec> consumer);

    }


    interface _FieldsColumnsSpec<I extends Item, T> extends _LinesSpec<I, T> {

        _LinesSpec<I, T> fields(Consumer<_ColumnTerminatedBySpec> consumer);

        _LinesSpec<I, T> columns(Consumer<_ColumnTerminatedBySpec> consumer);

        _LinesSpec<I, T> ifFields(Consumer<_ColumnTerminatedBySpec> consumer);

        _LinesSpec<I, T> ifColumns(Consumer<_ColumnTerminatedBySpec> consumer);
    }


    interface _CharsetSpec<I extends Item, T> extends _FieldsColumnsSpec<I, T> {

        _FieldsColumnsSpec<I, T> characterSet(String charsetName);

        _FieldsColumnsSpec<I, T> ifCharacterSet(Supplier<String> supplier);

    }

    interface _PartitionSpec<I extends Item, T> extends _PartitionClause<_CharsetSpec<I, T>>,
            _CharsetSpec<I, T> {

    }


    interface _ChildIntoTableClause<I extends Item, P> {

        <T> _PartitionSpec<I, T> intoTable(ComplexTableMeta<P, T> table);
    }

    interface _ChildStrategyOptionSpec<I extends Item, P> extends _ChildIntoTableClause<I, P> {

        _ChildIntoTableClause<I, P> replace();

        _ChildIntoTableClause<I, P> ignore();

        _ChildIntoTableClause<I, P> ifReplace(BooleanSupplier predicate);

        _ChildIntoTableClause<I, P> ifIgnore(BooleanSupplier predicate);

    }

    interface _ChildLocalInfileClause<I extends Item, P> {

        _ChildStrategyOptionSpec<I, P> infile(Path filePath);

        _ChildStrategyOptionSpec<I, P> infile(Supplier<Path> supplier);

    }


    interface _ChildLoadDataClause<I extends Item, P> {

        _ChildLocalInfileClause<I, P> loadData(MySQLs.Modifier local);

        _ChildLocalInfileClause<I, P> loadData(List<MySQLs.Modifier> modifierList);

    }

    interface _ChildLoadData<I extends Item, P> extends DmlCommand {

        _ChildLoadDataClause<I, P> child();

    }


    interface _IntoTableClause<I extends Item> {

        <T> _PartitionSpec<I, T> intoTable(SimpleTableMeta<T> table);

        <T> _PartitionSpec<_ChildLoadData<I, T>, T> intoTable(ParentTableMeta<T> table);

    }

    interface _StrategyOptionSpec<I extends Item> extends _IntoTableClause<I> {

        _IntoTableClause<I> replace();

        _IntoTableClause<I> ignore();

        _IntoTableClause<I> ifReplace(BooleanSupplier predicate);

        _IntoTableClause<I> ifIgnore(BooleanSupplier predicate);

    }

    interface _LocalInfileClause<I extends Item> {

        _StrategyOptionSpec<I> infile(Path filePath);

        _StrategyOptionSpec<I> infile(Supplier<Path> supplier);

    }


    interface _LoadDataClause<I extends Item> {

        _LocalInfileClause<I> loadData(MySQLs.Modifier local);

        _LocalInfileClause<I> loadData(List<MySQLs.Modifier> modifierList);

    }


}
