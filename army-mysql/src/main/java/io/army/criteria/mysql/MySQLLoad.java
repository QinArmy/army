package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.domain.IDomain;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.ParentTableMeta;
import io.army.meta.SimpleTableMeta;

import java.nio.file.Path;
import java.util.List;
import java.util.function.*;

public interface MySQLLoad extends DialectStatement, PrimaryStatement {


    interface _LoadDataSpec {

        MySQLLoad asLoadData();

    }

    interface _LoadSetSpec<C, T extends IDomain> extends Insert._AssignmentSetClause<C, T, _LoadSetSpec<C, T>>
            , _LoadDataSpec {

    }

    interface _StaticColumnDualClause<VR> extends Statement._RightParenClause<VR> {

        Statement._RightParenClause<VR> comma(Expression fieldOrVar);

        _StaticColumnDualClause<VR> comma(Expression fieldOrVar1, Expression fieldOrVar2);

    }


    interface _ColumnOrVarListClause<C, VR> {


        Statement._RightParenClause<VR> leftParen(Consumer<Consumer<Expression>> consumer);

        Statement._RightParenClause<VR> leftParen(BiConsumer<C, Consumer<Expression>> consumer);

        Statement._RightParenClause<VR> leftParen(Expression fieldOrVar);

        _StaticColumnDualClause<VR> leftParen(Expression fieldOrVar1, Expression fieldOrVar2);

    }

    interface _ColumnOrVarListSpec<C, T extends IDomain>
            extends _ColumnOrVarListClause<C, _LoadSetSpec<C, FieldMeta<T>>>
            , _LoadSetSpec<C, FieldMeta<T>> {

    }

    interface _LinesClause<LR> {

        LR lines();

    }

    interface _LineAfterIgnoreClause<LR> {

        LR rows();

    }


    interface _IgnoreLineClause<C, GR> {

        _LineAfterIgnoreClause<GR> ignore(long rowNumber);

        _LineAfterIgnoreClause<GR> ignore(Supplier<Long> supplier);

        _LineAfterIgnoreClause<GR> ignore(Function<C, Long> function);

        _LineAfterIgnoreClause<GR> ifIgnore(Supplier<Long> supplier);

        _LineAfterIgnoreClause<GR> ifIgnore(Function<C, Long> function);
    }

    interface _IgnoreLineSpec<C, T extends IDomain> extends _IgnoreLineClause<C, _ColumnOrVarListSpec<C, T>>
            , _ColumnOrVarListSpec<C, T> {

    }

    interface _TerminatedByClause<C, TR> {

        TR terminatedBy(String string);

        TR terminatedBy(Supplier<String> supplier);

        TR terminatedBy(Function<C, String> function);

        TR ifTerminatedBy(Supplier<String> supplier);

        TR ifTerminatedBy(Function<C, String> function);
    }

    interface _StartingByClause<C, SR> {

        SR startingBy(String string);

        SR startingBy(Supplier<String> supplier);

        SR startingBy(Function<C, String> function);

        SR ifStartingBy(Supplier<String> supplier);

        SR ifStartingBy(Function<C, String> function);
    }


    interface _LinesTerminatedBySpec<C, T extends IDomain> extends _TerminatedByClause<C, _IgnoreLineSpec<C, T>>
            , _IgnoreLineSpec<C, T> {

    }


    interface _LineStartingBySpec<C, T extends IDomain> extends _StartingByClause<C, _LinesTerminatedBySpec<C, T>>
            , _TerminatedByClause<C, _IgnoreLineSpec<C, T>> {

    }


    interface _LinesBeforeIgnoreSpec<C, T extends IDomain> extends _LinesClause<_LineStartingBySpec<C, T>>
            , _IgnoreLineSpec<C, T> {

    }


    interface _EnclosedByClause<C, ER> {

        ER enclosedBy(char ch);

        ER enclosedBy(Supplier<Character> supplier);

        ER enclosedBy(Function<C, Character> function);

        ER ifEnclosedBy(Supplier<Character> supplier);

        ER ifEnclosedBy(Function<C, Character> function);
    }

    interface _OptionallyClause<C, OR> {

        OR optionally();

        OR ifOptionally(Supplier<Boolean> supplier);

        OR ifOptionally(Predicate<C> predicate);
    }


    interface _EscapedByClause<C, CR> {

        CR escapedBy(char ch);

        CR escapedBy(Supplier<Character> supplier);

        CR escapedBy(Function<C, Character> function);

        CR ifEscapedBy(Supplier<Character> supplier);

        CR ifEscapedBy(Function<C, Character> function);
    }


    interface _ColumnEscapedByClause<C, T extends IDomain>
            extends _EscapedByClause<C, _LinesBeforeIgnoreSpec<C, T>> {

    }

    interface _ColumnEscapedBySpec<C, T extends IDomain> extends _ColumnEscapedByClause<C, T>
            , _LinesBeforeIgnoreSpec<C, T> {

    }

    interface _ColumnEnclosedByClause<C, T extends IDomain>
            extends _EnclosedByClause<C, _ColumnEscapedBySpec<C, T>> {

    }


    interface _ColumnEnclosedBySpec<C, T extends IDomain>
            extends _ColumnEnclosedByClause<C, T>, _ColumnEscapedBySpec<C, T>
            , _OptionallyClause<C, _ColumnEnclosedByClause<C, T>> {


    }


    interface _ColumnTerminatedBySpec<C, T extends IDomain>
            extends _TerminatedByClause<C, _IgnoreLineSpec<C, T>>
            , _OptionallyClause<C, _ColumnEnclosedByClause<C, T>>
            , _ColumnEnclosedByClause<C, T>
            , _ColumnEscapedByClause<C, T> {

        @Override
        _ColumnEnclosedBySpec<C, T> terminatedBy(String string);

        @Override
        _ColumnEnclosedBySpec<C, T> terminatedBy(Supplier<String> supplier);

        @Override
        _ColumnEnclosedBySpec<C, T> terminatedBy(Function<C, String> function);

        @Override
        _ColumnEnclosedBySpec<C, T> ifTerminatedBy(Supplier<String> supplier);

        @Override
        _ColumnEnclosedBySpec<C, T> ifTerminatedBy(Function<C, String> function);

    }

    interface _FieldsColumnsClause<FR> {

        FR fields();

        FR columns();
    }


    interface _FieldsColumnsSpec<C, T extends IDomain> extends _FieldsColumnsClause<_ColumnTerminatedBySpec<C, T>>
            , _LinesBeforeIgnoreSpec<C, T> {

    }

    interface _CharsetClause<C, SR> {

        SR characterSet(String charsetName);

        SR characterSet(Supplier<String> supplier);

        SR characterSet(Function<C, String> function);

        SR ifCharacterSet(Supplier<String> supplier);

        SR ifCharacterSet(Function<C, String> function);

    }

    interface _CharsetSpec<C, T extends IDomain> extends _CharsetClause<C, _FieldsColumnsSpec<C, T>>
            , _FieldsColumnsSpec<C, T> {
    }

    interface _PartitionSpec<C, T extends IDomain> extends MySQLQuery._PartitionClause<C, _CharsetSpec<C, T>>
            , _CharsetSpec<C, T> {

    }


    interface _StrategyOptionClause<C, OR> {

        OR replace();

        OR ifReplace(Supplier<Boolean> supplier);

        OR ifReplace(Predicate<C> predicate);

        OR ignore();

        OR ifIgnore(Supplier<Boolean> supplier);

        OR ifIgnore(Predicate<C> predicate);

    }


    interface _LoadInfileClause<C, FR> {

        FR infile(Path filePath);

        FR infile(Supplier<Path> supplier);

        FR infile(Function<C, Path> function);
    }

    interface _ChildIntoTableClause<C, P extends IDomain> {

        <T extends IDomain> _PartitionSpec<C, T> intoTable(ComplexTableMeta<P, T> table);
    }

    interface _ChildLoadStrategySpec<C, P extends IDomain> extends _StrategyOptionClause<C, _ChildIntoTableClause<C, P>>
            , _ChildIntoTableClause<C, P> {

    }


    interface _ChildLoadInfileClause<C, P extends IDomain> extends _LoadInfileClause<C, _ChildLoadStrategySpec<C, P>> {

    }


    interface _ParentAssignmentSetSpec<C, T extends IDomain>
            extends Insert._AssignmentSetClause<C, FieldMeta<T>, _ParentAssignmentSetSpec<C, T>> {

        _ChildLoadInfileClause<C, T> child(List<MySQLWords> modifierList);

    }


    interface _ParentColumnVarListSpec<C, T extends IDomain>
            extends _ColumnOrVarListClause<C, _ParentAssignmentSetSpec<C, T>>
            , _ParentAssignmentSetSpec<C, T> {
    }


    interface _ParentIgnoreLineSpec<C, T extends IDomain>
            extends _IgnoreLineClause<C, _ParentColumnVarListSpec<C, T>>
            , _ParentColumnVarListSpec<C, T> {

    }

    interface _ParentLineTerminatedByClause<C, T extends IDomain>
            extends _TerminatedByClause<C, _ParentIgnoreLineSpec<C, T>>
            , _ParentIgnoreLineSpec<C, T> {

    }

    interface _ParentLineTerminatedBySpec<C, T extends IDomain>
            extends _ParentLineTerminatedByClause<C, T>
            , _ParentIgnoreLineSpec<C, T> {

    }


    interface _ParentLineStartingBySpec<C, T extends IDomain>
            extends _StartingByClause<C, _ParentLineTerminatedBySpec<C, T>>
            , _ParentLineTerminatedByClause<C, T> {


    }


    interface _ParentLinesSpec<C, T extends IDomain> extends _LinesClause<_ParentLineStartingBySpec<C, T>>
            , _ParentIgnoreLineSpec<C, T> {

    }

    interface _ParentColumnEscapedByClause<C, T extends IDomain>
            extends _EscapedByClause<C, _ParentLinesSpec<C, T>> {

    }

    interface _ParentColumnEscapedBySpec<C, T extends IDomain>
            extends _ParentColumnEscapedByClause<C, T>, _ParentLinesSpec<C, T> {

    }


    interface _ParentColumnEnclosedByClause<C, T extends IDomain>
            extends _EnclosedByClause<C, _ParentColumnEscapedBySpec<C, T>> {

    }


    interface _ParentColumnEnclosedBySpec<C, T extends IDomain>
            extends _OptionallyClause<C, _ParentColumnEnclosedByClause<C, T>>
            , _ParentColumnEnclosedByClause<C, T>
            , _ParentColumnEscapedBySpec<C, T> {

    }


    interface _ParentColumnTerminatedBySpec<C, T extends IDomain>
            extends _TerminatedByClause<C, _ParentIgnoreLineSpec<C, T>>
            , _OptionallyClause<C, _ParentColumnEnclosedByClause<C, T>>
            , _ParentColumnEnclosedByClause<C, T>
            , _ParentColumnEscapedByClause<C, T> {

        @Override
        _ParentColumnEnclosedBySpec<C, T> terminatedBy(String string);

        @Override
        _ParentColumnEnclosedBySpec<C, T> terminatedBy(Supplier<String> supplier);

        @Override
        _ParentColumnEnclosedBySpec<C, T> terminatedBy(Function<C, String> function);

        @Override
        _ParentColumnEnclosedBySpec<C, T> ifTerminatedBy(Supplier<String> supplier);

        @Override
        _ParentColumnEnclosedBySpec<C, T> ifTerminatedBy(Function<C, String> function);
    }


    interface _ParentFieldsColumnsSpec<C, T extends IDomain>
            extends _FieldsColumnsClause<_ParentColumnTerminatedBySpec<C, T>>, _ParentLinesSpec<C, T> {

    }


    interface _ParentCharsetSpec<C, T extends IDomain> extends _CharsetClause<C, _ParentFieldsColumnsSpec<C, T>>
            , _ParentFieldsColumnsSpec<C, T> {

    }


    interface _ParentPartitionSpec<C, T extends IDomain>
            extends MySQLQuery._PartitionClause<C, _ParentCharsetSpec<C, T>>
            , _ParentCharsetSpec<C, T> {

    }

    interface _IntoTableClause<C> {

        <T extends IDomain> _PartitionSpec<C, T> intoTable(SimpleTableMeta<T> table);

        <T extends IDomain> _ParentPartitionSpec<C, T> intoTable(ParentTableMeta<T> table);

    }

    interface _StrategyOptionSpec<C> extends _StrategyOptionClause<C, _IntoTableClause<C>>, _IntoTableClause<C> {

    }


    interface _LoadDataClause<C> {

        _LoadInfileClause<C, _StrategyOptionSpec<C>> loadData(List<MySQLWords> modifierList);

    }


}
