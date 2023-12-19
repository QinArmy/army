package io.army.dialect;

import io.army.criteria.LiteralMode;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

import javax.annotation.Nullable;

public interface _InsertContext extends _DmlContext, _SetClauseContext {

    TableMeta<?> insertTable();

    LiteralMode literalMode();

    @Nullable
    String tableAlias();

    @Nullable
    String safeTableAlias();

    @Nullable
    String safeTableName();

    @Nullable
    String rowAlias();

    @Nullable
    String safeRowAlias();

    boolean hasConditionPredicate();


    void appendConditionPredicate(boolean firstPredicate);

    @Override
    _InsertContext parentContext();

    @Override
    SimpleStmt build();

    interface _ColumnListSpec {

        void appendFieldList();

    }

    interface _ReturningIdSpec {
        void appendReturnIdIfNeed();

    }

    interface _AssignmentsSpec extends _ReturningIdSpec {

        void appendAssignmentClause();
    }


    interface _ValueSyntaxSpec extends _ColumnListSpec, _ReturningIdSpec {

        void appendValueList();

    }

    interface _QuerySyntaxSpec extends _ColumnListSpec {

        void appendSubQuery();
    }


}
