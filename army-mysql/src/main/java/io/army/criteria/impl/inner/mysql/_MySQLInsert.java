package io.army.criteria.impl.inner.mysql;

import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.inner._Insert;

import java.util.List;

public interface _MySQLInsert extends _Insert, _Insert._ConflictActionClauseSpec, _Insert._SupportConflictClauseSpec {

    List<Hint> hintList();

    List<MySQLs.Modifier> modifierList();

    List<String> partitionList();



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

    interface _MySQLParentQueryInsert extends _MySQLQueryInsert, _ParentQueryInsert {

    }

    interface _MySQLChildQueryInsert extends _Insert._ChildQueryInsert, _MySQLQueryInsert {

        @Override
        _MySQLParentQueryInsert parentStmt();

    }


}
