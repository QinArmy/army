package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.criteria.SubQuery;
import io.army.criteria.impl._Pair;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();


    interface _DuplicateKeyClause {

    }

    interface _SupportReturningClause {

    }


    interface _InsertOption {

        boolean isMigration();

        boolean isPreferLiteral();
    }


    interface _ColumnListInsert extends _Insert {

        /**
         * @return a unmodifiable list , maybe empty.
         */
        List<FieldMeta<?>> fieldList();

        Map<FieldMeta<?>, Boolean> fieldMap();
    }

    interface _ChildInsert extends _Insert {

        _Insert parentStmt();
    }


    interface _ValuesSyntaxInsert extends _ColumnListInsert, _InsertOption {

        @Nullable
        NullHandleMode nullHandle();


        Map<FieldMeta<?>, _Expression> defaultValueMap();

    }


    interface _ValuesInsert extends _ValuesSyntaxInsert {


        List<Map<FieldMeta<?>, _Expression>> rowPairList();


    }


    interface _ChildValuesInsert extends _ValuesInsert, _ChildInsert {

        @Override
        _ValuesInsert parentStmt();
    }


    interface _DomainInsert extends _ValuesSyntaxInsert {


        List<?> domainList();

    }

    interface _ChildDomainInsert extends _DomainInsert, _ChildInsert {

        @Override
        _DomainInsert parentStmt();
    }

    interface _AssignmentStatementSpec {

        List<_Pair<FieldMeta<?>, _Expression>> pairList();

        Map<FieldMeta<?>, _Expression> pairMap();
    }


    interface _AssignmentInsert extends _Insert, _InsertOption, _AssignmentStatementSpec {


        boolean isPreferLiteral();

    }

    interface _ChildAssignmentInsert extends _AssignmentInsert, _ChildInsert {

        _AssignmentInsert parentStmt();
    }


    interface _QueryInsert extends _ColumnListInsert {

        SubQuery subQuery();

    }

    interface _ChildQueryInsert extends _QueryInsert, _ChildInsert {

        _QueryInsert parentStmt();
    }


}
