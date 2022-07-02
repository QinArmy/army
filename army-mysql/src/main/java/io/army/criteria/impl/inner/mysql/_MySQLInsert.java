package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Hint;
import io.army.criteria.impl.inner._DomainInsert;
import io.army.criteria.impl.inner._Expression;
import io.army.criteria.impl.inner._Insert;
import io.army.criteria.mysql.MySQLWords;
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

        String rowAlias();

        Map<String, FieldMeta<?>> aliasToField();

    }


    interface _MySQLDomainInsert extends _DomainInsert, _MySQLInsert {


    }


}
