package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Hint;
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

    List<String> childPartitionList();


    interface _InsertWithDuplicateKey {

        Map<?, _Expression> valuePairsForDuplicate();

    }

    interface _InsertWithRowAlias extends _InsertWithDuplicateKey {

        @Nullable
        String rowAlias();

        Map<String, FieldMeta<?>> aliasToField();

    }


    /**
     * <p>
     * If {@link  _MySQLValueInsert} implementation implements this interface,use ROW syntax
     * </p>
     */
    interface _MySQLRowSetRowSyntax {

    }


    interface _MySQLDomainInsert extends _Insert._DomainInsert, _MySQLInsert {


    }

    interface _MySQLValueInsert extends _Insert._ValueInsert, _MySQLInsert {

    }

    interface _MySQLAssignmentInsert extends _Insert._AssignmentInsert, _MySQLInsert {

    }

    interface _MySQLRowSetInsert extends _Insert._RowSetInsert, _MySQLInsert {

    }


}
