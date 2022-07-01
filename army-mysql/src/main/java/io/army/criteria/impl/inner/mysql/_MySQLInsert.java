package io.army.criteria.impl.inner.mysql;

import io.army.criteria.impl.inner._DomainInsert;
import io.army.criteria.impl.inner._Insert;

public interface _MySQLInsert extends _Insert {

    interface _MySQLDomainInsert extends _DomainInsert, _MySQLInsert {


    }

}
