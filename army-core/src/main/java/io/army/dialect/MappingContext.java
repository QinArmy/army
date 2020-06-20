package io.army.dialect;

import java.time.ZoneId;

public interface MappingContext {

    ZoneId zoneId();

    SQLDialect sqlDialect();
}
