package io.army.criteria.postgre;

import io.army.criteria.Expression;

public interface PgObjectConsumer {

    PostgreStatement._PgExpObjectValueClause comma(String keName);

    PostgreStatement._PgExpObjectValueClause comma(Expression keyExp);


}
