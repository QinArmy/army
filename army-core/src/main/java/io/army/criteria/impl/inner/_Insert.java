package io.army.criteria.impl.inner;

import io.army.criteria.*;
import io.army.criteria.impl._Pair;

import javax.annotation.Nullable;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;

import java.util.List;
import java.util.Map;

public interface _Insert extends _Statement {

    TableMeta<?> table();

    @Nullable
    String tableAlias();

    /**
     * @return non-zero,negative : query insert.
     */
    int insertRowCount();

    interface _SupportConflictClauseSpec {

        @Nullable
        String rowAlias();

        boolean hasConflictAction();

        /**
         * @return true: possibly ignore when conflict
         */
        boolean isIgnorableConflict();

        boolean isDoNothing();

    }

    interface _ConflictActionClauseSpec extends _SupportConflictClauseSpec {

        List<_ItemPair> updateSetClauseList();


    }

    interface _ParentSubInsert extends SubStatement {

        TableMeta<?> domainTable();
    }


    interface _OneStmtChildInsert {

        void validParentDomain();
    }


    interface _ConflictActionPredicateClauseSpec {

        List<_Predicate> updateSetPredicateList();
    }


    /**
     * <p>
     * This interface representing dialect support sub insert statement in with clause,for example PostgreSQL.
     * </p>
     */
    interface _SupportWithClauseInsert extends _Insert, _WithClauseSpec {

    }


    interface _InsertOption {

        boolean isMigration();


        LiteralMode literalMode();

    }


    interface _ColumnListInsert extends _Insert {

        /**
         * @return a unmodifiable list ,non-empty.
         */
        List<FieldMeta<?>> fieldList();

        Map<FieldMeta<?>, Boolean> fieldMap();
    }

    interface _ChildInsert extends _Insert, _Statement._ChildStatement {

        @Override
        _Insert parentStmt();
    }


    interface _ValuesSyntaxInsert extends _ColumnListInsert, _InsertOption {

        boolean isIgnoreReturnIds();


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


        SubQuery subQuery();

    }

    /**
     * Primary parent query insert
     *
     * @see _ParentSubInsert
     */
    interface _ParentQueryInsert extends _QueryInsert, PrimaryStatement {

        @Deprecated
        CodeEnum discriminatorEnum();
    }

    interface _ChildQueryInsert extends _QueryInsert, _ChildInsert {

        _ParentQueryInsert parentStmt();

    }


    interface _SubInsert {

        @Nullable
        CodeEnum discriminatorValue();
    }


}
