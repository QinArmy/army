package io.army.dialect;

import io.army.session.Database;

import java.time.ZoneId;

public interface MappingContext {

    ZoneId zoneId();

    Database database();
}
