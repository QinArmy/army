package io.army.dialect;

import io.army.SessionFactory;
import io.army.meta.mapping.MappingType;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

    SessionFactory sessionFactory();

    default String mapping(MappingType mappingType) {
        throw new UnsupportedOperationException();
    }

}
