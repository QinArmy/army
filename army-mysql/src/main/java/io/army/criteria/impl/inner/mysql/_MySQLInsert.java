package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Hint;
import io.army.criteria.impl._Pair;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.mysql.MySQLWords;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _MySQLInsert extends _Insert {

    List<Hint> hintList();

    List<MySQLWords> modifierList();

    List<String> partitionList();



    interface _InsertWithDuplicateKey extends _Insert._DuplicateKeyClause {

        List<_Pair<Object, _Expression>> duplicatePairList();

    }

    interface _InsertWithRowAlias extends _InsertWithDuplicateKey {

        @Nullable
        String rowAlias();

        Map<String, FieldMeta<?>> aliasToField();

    }


    interface _MySQLDomainInsert extends _Insert._DomainInsert, _MySQLInsert {


    }

    interface _MySQLValueInsert extends _ValuesInsert, _MySQLInsert {

    }

    interface _MySQLAssignmentInsert extends _Insert._AssignmentInsert, _MySQLInsert {

    }

    interface _MySQQueryInsert extends _QueryInsert, _MySQLInsert {

    }

    interface _MySQLReplace {

    }


}
