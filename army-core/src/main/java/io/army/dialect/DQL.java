package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.Visible;
import io.army.wrapper.SelectSQLWrapper;

import java.util.List;

public interface DQL extends SQL {

    List<SelectSQLWrapper> select(Select select, Visible visible);

    void select(Select select, SQLContext original);


    void subQuery(SubQuery subQuery, SQLContext original);

}
