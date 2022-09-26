package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Hint;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.mysql.MySQLModifier;
import io.army.meta.FieldMeta;

import java.util.List;

public interface _MySQLInsert extends _Insert {

    List<Hint> hintList();

    List<MySQLModifier> modifierList();

    List<String> partitionList();



    interface _InsertWithDuplicateKey extends _SupportConflictClauseSpec {

        List<_Pair<FieldMeta<?>, _Expression>> duplicatePairList();

    }




    interface _MySQLDomainInsert extends _Insert._DomainInsert, _MySQLInsert {


    }

    interface _MySQLValueInsert extends _ValuesInsert, _MySQLInsert {

    }

    interface _MySQLAssignmentInsert extends _Insert._AssignmentInsert, _MySQLInsert {

    }

    interface _MySQQueryInsert extends _QueryInsert, _MySQLInsert {

    }



}
