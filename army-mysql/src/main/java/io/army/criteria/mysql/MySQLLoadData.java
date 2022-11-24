package io.army.criteria.mysql;

import io.army.criteria.Expression;
import io.army.criteria.Insert;
import io.army.criteria.Item;
import io.army.criteria.Statement;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.impl.MySQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SingleTableMeta;

import java.nio.file.Path;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface MySQLLoadData extends MySQLStatement, SQLCommand {


    interface _LoadSetSpec<I extends Item, T> extends Insert._StaticAssignmentSetClause<T, _LoadSetSpec<I, T>>
            , _AsCommandClause<I> {

    }

    interface _StaticColumnDualClause<VR> extends Statement._RightParenClause<VR> {

        Statement._RightParenClause<VR> comma(Expression fieldOrVar);

        _StaticColumnDualClause<VR> comma(Expression fieldOrVar1, Expression fieldOrVar2);

    }


    interface _ColumnOrVarListClause<VR> {


        Statement._RightParenClause<VR> leftParen(Consumer<Consumer<Expression>> consumer);

        Statement._RightParenClause<VR> leftParen(Expression fieldOrVar);

        _StaticColumnDualClause<VR> leftParen(Expression fieldOrVar1, Expression fieldOrVar2);

    }

    interface _ColumnOrVarListSpec<I extends Item, T> extends _ColumnOrVarListClause<_LoadSetSpec<I, T>>
            , _LoadSetSpec<I, T> {

    }


    interface _LineAfterIgnoreClause<I extends Item, T> {

        _ColumnOrVarListSpec<I, T> rows();

    }


    interface _IgnoreLineSpec<I extends Item, T> extends _ColumnOrVarListSpec<I, T> {

        _LineAfterIgnoreClause<I, T> ignore(long rowNumber);

        _LineAfterIgnoreClause<I, T> ignore(Supplier<Long> supplier);

        _LineAfterIgnoreClause<I, T> ifIgnore(Supplier<Long> supplier);
    }

    interface _TerminatedByClause<I extends Item, T> {

        _IgnoreLineSpec<I, T> terminatedBy(String string);

        _IgnoreLineSpec<I, T> terminatedBy(Supplier<String> supplier);

        _IgnoreLineSpec<I, T> ifTerminatedBy(Supplier<String> supplier);

    }


    interface _LinesTerminatedBySpec<I extends Item, T> extends _TerminatedByClause<I, T>
            , _IgnoreLineSpec<I, T> {

    }


    interface _LineStartingBySpec<I extends Item, T> extends _TerminatedByClause<I, T> {

        _LinesTerminatedBySpec<I, T> startingBy(String string);

        _LinesTerminatedBySpec<I, T> startingBy(Supplier<String> supplier);

        _LinesTerminatedBySpec<I, T> ifStartingBy(Supplier<String> supplier);
    }


    interface _LinesSpec<I extends Item, T> extends _IgnoreLineSpec<I, T> {

        _LineStartingBySpec<I, T> lines();

    }


    interface _ColumnEscapedBySpec<I extends Item, T> extends _LinesSpec<I, T> {

        _LinesSpec<I, T> escapedBy(char ch);

        _LinesSpec<I, T> escapedBy(Supplier<Character> supplier);

        _LinesSpec<I, T> ifEscapedBy(Supplier<Character> supplier);

    }

    interface _ColumnEnclosedByClause<I extends Item, T> {

        _ColumnEscapedBySpec<I, T> enclosedBy(char ch);

        _ColumnEscapedBySpec<I, T> enclosedBy(Supplier<Character> supplier);

        _ColumnEscapedBySpec<I, T> ifEnclosedBy(Supplier<Character> supplier);
    }

    interface _OptionallySpec<I extends Item, T> extends _ColumnEnclosedByClause<I, T> {

        _ColumnEnclosedByClause<I, T> optionally();

        _ColumnEnclosedByClause<I, T> ifOptionally(BooleanSupplier predicate);
    }


    interface _ColumnEnclosedBySpec<I extends Item, T> extends _OptionallySpec<I, T>
            , _ColumnEscapedBySpec<I, T> {

    }


    interface _ColumnTerminatedBySpec<I extends Item, T>
            extends _TerminatedByClause<I, T>
            , _ColumnEnclosedBySpec<I, T> {

        @Override
        _ColumnEnclosedBySpec<I, T> terminatedBy(String string);

        @Override
        _ColumnEnclosedBySpec<I, T> terminatedBy(Supplier<String> supplier);

        @Override
        _ColumnEnclosedBySpec<I, T> ifTerminatedBy(Supplier<String> supplier);


    }

    interface _FieldsColumnsSpec<I extends Item, T> extends _LinesSpec<I, T> {

        _ColumnTerminatedBySpec<I, T> fields();

        _ColumnTerminatedBySpec<I, T> columns();
    }


    interface _CharsetSpec<I extends Item, T> extends _FieldsColumnsSpec<I, T> {

        _FieldsColumnsSpec<I, T> characterSet(String charsetName);

        _FieldsColumnsSpec<I, T> characterSet(MySQLCharset charset);

        _FieldsColumnsSpec<I, T> ifCharacterSet(Supplier<String> supplier);

    }

    interface _PartitionSpec<I extends Item, T> extends _PartitionClause_0<_CharsetSpec<I, T>>
            , _CharsetSpec<I, T> {

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

    interface _ParentLoadData<I extends Item, P> extends SQLCommand {

        _ChildLoadDataClause<I, P> child();

    }


    interface _IntoTableClause<I extends Item> {

        <T> _PartitionSpec<I, T> intoTable(SingleTableMeta<T> table);

        <T> _PartitionSpec<_ParentLoadData<I, T>, T> intoTable(ParentTableMeta<T> table);

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
