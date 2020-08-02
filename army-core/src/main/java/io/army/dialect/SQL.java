package io.army.dialect;

import io.army.GenericRmSessionFactory;
import io.army.meta.mapping.MappingMeta;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

    boolean supportZone();

    GenericRmSessionFactory sessionFactory();

    String mapping(MappingMeta mappingType);

    Database database();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

    boolean hasRowKeywords();

}
