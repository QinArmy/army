package io.army;

import java.time.ZoneId;
import java.util.List;

public interface SessionFactoryOptions {

    boolean isShowSql();

    SessionFactoryOptions showSql(boolean showSql);

    boolean isFormatSql();

    SessionFactoryOptions formatSql(boolean formatSql);

    boolean isReadonly();

    ZoneId zoneId();

    /**
     *
     * @return a unmodifiable List
     */
    List<String> packagesToScan();

}
