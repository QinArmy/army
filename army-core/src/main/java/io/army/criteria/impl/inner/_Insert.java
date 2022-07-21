package io.army.criteria.impl.inner;

import io.army.criteria.ItemPair;
import io.army.criteria.NullHandleMode;
import io.army.criteria.SubQuery;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();

    /**
     * @return a unmodifiable list , maybe empty.
     */
    List<FieldMeta<?>> fieldList();

    Map<FieldMeta<?>, Boolean> fieldMap();


    interface _DuplicateKeyClause {

    }


    interface _InsertOption {

        boolean isMigration();

        @Nullable
        NullHandleMode nullHandle();
    }


    interface _ValuesSyntaxInsert extends _Insert, _InsertOption {

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


        List<IDomain> domainList();

    }

    interface _ChildDomainInsert extends _DomainInsert {

        _DomainInsert parentStmt();
    }

    interface _AssignmentStatementSpec {

        List<ItemPair> rowPairList();

        Map<FieldMeta<?>, Boolean> fieldMap();
    }


    interface _AssignmentInsert extends _Insert, _InsertOption, _AssignmentStatementSpec {

        /**
         * @return always {@link  Collections#emptyList()}
         */
        @Override
        List<FieldMeta<?>> fieldList();

        boolean isPreferLiteral();

    }

    interface _ChildAssignmentInsert extends _AssignmentInsert {

        _AssignmentInsert parentStmt();
    }


    interface _QueryInsert extends _Insert {

        SubQuery subQuery();

    }

    interface _ChildQueryInsert extends _QueryInsert {

        _QueryInsert parentStmt();
    }

    interface _ReturningInsert extends _Insert {


    }


}
