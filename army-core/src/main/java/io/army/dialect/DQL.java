package io.army.dialect;

import io.army.criteria.*;
import io.army.wrapper.SQLWrapper;

import java.util.List;

public interface DQL extends SQL {

    List<SQLWrapper> select(Select select, Visible visible);

    void select(Select select, SQLContext originalContext);

    void partSelect(PartQuery select, SQLContext originalContext);

    void partSubQuery(PartQuery subQuery, SQLContext originalContext);

    void subQuery(SubQuery subQuery, SQLContext originalContext);

}
