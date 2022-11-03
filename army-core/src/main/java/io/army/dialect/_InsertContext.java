package io.army.dialect;

import io.army.criteria.LiteralMode;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

public interface _InsertContext extends StmtContext {

    TableMeta<?> insertTable();

    LiteralMode literalMode();

    @Nullable
    String safeRowAlias();


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
