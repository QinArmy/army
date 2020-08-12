package io.army.dialect;

import io.army.GenericRmSessionFactory;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

    boolean supportZone();

    GenericRmSessionFactory sessionFactory();

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();

}
