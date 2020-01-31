package io.army.dialect;

import io.army.SessionFactory;

import java.time.ZoneId;

public interface SQL {

    String quoteIfNeed(String identifier);

    boolean isKeyWord(String identifier);

    ZoneId zoneId();

   SessionFactory sessionFactory();

}
