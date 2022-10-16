package io.army.criteria.mysql;

import io.army.criteria.CteBuilderSpec;

public interface MySQLCteBuilder extends CteBuilderSpec {


    MySQLQuery._Ctel query(String cteName);


}
