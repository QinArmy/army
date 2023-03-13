package io.army.criteria.impl.inner;

import io.army.criteria.LiteralMode;
import io.army.criteria.NullMode;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl._Pair;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();

    interface _SupportConflictClauseSpec {

        boolean hasConflictAction();

    }

    interface _ConflictActionClauseSpec extends _SupportConflictClauseSpec {

        @Nullable
        String rowAlias();

        List<_ItemPair> updateSetClauseList();


    }


    interface _ConflictActionPredicateClauseSpec {

        List<_Predicate> updateSetPredicateList();
    }


    /**
     * <p>
     * This interface representing dialect support sub insert statement in with clause,for example PostgreSQL.
     * </p>
     */
    interface _SupportWithClauseInsert extends _Insert {

        boolean isRecursive();

        List<_Cte> cteList();
    }


    interface _InsertOption {

        boolean isMigration();


        LiteralMode literalMode();
    }


    interface _ColumnListInsert extends _Insert {

        /**
         * @return a unmodifiable list , maybe empty.
         */
        List<FieldMeta<?>> fieldList();

        Map<FieldMeta<?>, Boolean> fieldMap();
    }

    interface _ChildInsert extends _Insert, _Statement._ChildStatement {

        _Insert parentStmt();
    }


    interface _ValuesSyntaxInsert extends _ColumnListInsert, _InsertOption {


        NullMode nullHandle();


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

        List<_Pair<FieldMeta<?>, _Expression>> assignmentPairList();

        Map<FieldMeta<?>, _Expression> assignmentMap();
    }


    interface _AssignmentInsert extends _Insert, _InsertOption, _AssignmentStatementSpec {


    }

    interface _ChildAssignmentInsert extends _AssignmentInsert, _ChildInsert {

        _AssignmentInsert parentStmt();
    }


    interface _QueryInsert extends _ColumnListInsert {

        /**
         * <p>
         * This method is invoked by {@link io.army.dialect._QueryInsertContext} for validate parent discriminator.
         * </p>
         */
        void validateOnlyParen();

        SubQuery subQuery();

    }

    interface _ChildQueryInsert extends _QueryInsert, _ChildInsert {

        _QueryInsert parentStmt();
    }


    interface _SubInsert {

        @Nullable
        CodeEnum discriminatorValue();
    }


}
