package io.army.criteria.mysql;

import io.army.criteria.DialectStatement;
import io.army.criteria.Insert;
import io.army.criteria.PrimaryStatement;
import io.army.criteria.TableField;

import java.util.function.Function;
import java.util.function.Supplier;

public interface MySQLLoad extends DialectStatement, PrimaryStatement {


    interface _LoadDataSpec {

        MySQLLoad asLoadData();

    }

    interface _LoadSetSpec<C, F extends TableField> extends Insert._AssignmentSetClause<C, F, _LoadSetSpec<C, F>>
            , _LoadDataSpec {

    }

    interface _ColumnListSpec<C, F extends TableField> extends Insert._ColumnListClause<C, F, _LoadSetSpec<C, F>>
            , _LoadSetSpec<C, F> {

    }

    interface _LineAfterIgnoreClause<LR> {

        LR rows();

    }

    interface _IgnoreRowSpec<C, F extends TableField> {

        _LineAfterIgnoreClause<_ColumnListSpec<C, F>> ignore(long rowNumber);

        _LineAfterIgnoreClause<_ColumnListSpec<C, F>> ignore(Supplier<Long> supplier);

        _LineAfterIgnoreClause<_ColumnListSpec<C, F>> ignore(Function<C, Long> function);

    }

    interface _TerminatedByClause<C, F extends TableField, TR> {

        TR terminatedBy(String string);

        TR terminatedBy(Supplier<String> supplier);

        TR terminatedBy(Function<C, String> function);

        TR ifTerminatedBy(Supplier<String> supplier);

        TR ifTerminatedBy(Function<C, String> function);
    }

    interface _StartingByClause<C, F extends TableField, SR> {

        SR startingBy(String string);

        SR startingBy(Supplier<String> supplier);

        SR startingBy(Function<C, String> function);

        SR ifStartingBy(Supplier<String> supplier);

        SR ifStartingBy(Function<C, String> function);
    }


    interface _LinesBeforeIgnoreClause<C, F extends TableField> {

        _StartingByClause<C, F> lines();

    }

    interface _FieldsSpec<C, F extends TableField> extends _LinesBeforeIgnoreClause<C, F> {

        void fields();
    }


}
