package io.army.dialect;

import io.army.Database;

import java.time.ZoneId;

public interface MappingContext {

    ZoneId zoneId();

    Database database();
}
