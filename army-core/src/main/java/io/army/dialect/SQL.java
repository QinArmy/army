package io.army.dialect;

import io.army.GenericSessionFactory;
import io.army.meta.mapping.MappingType;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

    boolean supportZoneId();

    GenericSessionFactory sessionFactory();

    String mapping(MappingType mappingType);

    SQLDialect sqlDialect();

}
