package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.criteria.SubQuery;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();




    interface _DuplicateKeyClause {

    }


    interface _InsertOption {

        boolean isMigration();

        @Nullable
        NullHandleMode nullHandle();
    }


    interface _ColumnListInsert extends _Insert {

        /**
         * @return a unmodifiable list , maybe empty.
         */
        List<FieldMeta<?>> fieldList();

        Map<FieldMeta<?>, Boolean> fieldMap();
    }


    interface _ValuesSyntaxInsert extends _ColumnListInsert, _InsertOption {


        boolean isPreferLiteral();


        Map<FieldMeta<?>, _Expression> defaultValueMap();

    }


    interface _ValuesInsert extends _ValuesSyntaxInsert {


        List<Map<FieldMeta<?>, _Expression>> rowValuesList();


    }


    interface _ChildValuesInsert extends _ValuesInsert {

        _ValuesInsert parentStmt();
    }


    interface _DomainInsert extends _ValuesSyntaxInsert {


        List<?> domainList();

    }

    interface _ChildDomainInsert extends _DomainInsert {

        _DomainInsert parentStmt();
    }

    interface _AssignmentStatementSpec {

        List<_ItemPair._FieldItemPair> rowPairList();

        Map<FieldMeta<?>, _ItemPair._FieldItemPair> fieldMap();
    }


    interface _AssignmentInsert extends _Insert, _InsertOption, _AssignmentStatementSpec {


        boolean isPreferLiteral();

    }

    interface _ChildAssignmentInsert extends _AssignmentInsert {

        _AssignmentInsert parentStmt();
    }


    interface _QueryInsert extends _ColumnListInsert {

        SubQuery subQuery();

    }

    interface _ChildQueryInsert extends _QueryInsert {

        _QueryInsert parentStmt();
    }

    interface _ReturningInsert extends _Insert {


    }


}
