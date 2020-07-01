package io.army.dialect;

import io.army.GenericSessionFactory;
import io.army.meta.mapping.MappingMeta;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

    boolean supportZone();

    GenericSessionFactory sessionFactory();

    String mapping(MappingMeta mappingType);

    SQLDialect sqlDialect();

    boolean tableAliasAfterAs();

    boolean singleDeleteHasTableAlias();

}
