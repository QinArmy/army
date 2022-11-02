package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.List;

public interface _MySQLInsert extends _Insert, _Insert._ConflictActionClauseSpec, _Insert._SupportConflictClauseSpec {

    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();

    List<String> partitionList();

    @Nullable
    String rowAlias();


    @Deprecated
    interface _InsertWithDuplicateKey extends _SupportConflictClauseSpec {

        List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList();

    }


    interface _MySQLDomainInsert extends _Insert._DomainInsert, _MySQLInsert {


    }

    interface _MySQLChildDomainInsert extends _Insert._ChildDomainInsert, _MySQLDomainInsert {

        @Override
        _MySQLDomainInsert parentStmt();

    }

    interface _MySQLValueInsert extends _ValuesInsert, _MySQLInsert {

    }

    interface _MySQLChildValueInsert extends _Insert._ChildValuesInsert, _MySQLValueInsert {

        @Override
        _MySQLValueInsert parentStmt();

    }

    interface _MySQLAssignmentInsert extends _Insert._AssignmentInsert, _MySQLInsert {

    }

    interface _MySQLChildAssignmentInsert extends _Insert._ChildAssignmentInsert, _MySQLAssignmentInsert {

        @Override
        _MySQLAssignmentInsert parentStmt();

    }

    interface _MySQLQueryInsert extends _QueryInsert, _MySQLInsert {

    }

    interface _MySQLChildQueryInsert extends _Insert._ChildQueryInsert, _MySQLQueryInsert {

        @Override
        _MySQLQueryInsert parentStmt();

    }


}
