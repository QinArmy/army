package io.army.dialect;

import io.army.criteria.LiteralMode;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.stmt.SimpleStmt;

public interface _InsertContext extends DmlContext {

    TableMeta<?> insertTable();

    LiteralMode literalMode();

    @Nullable
    String safeRowAlias();

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
