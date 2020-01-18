package io.army;

import java.time.ZoneId;
import java.util.List;

public interface SessionFactoryOptions {

    boolean isShowSql();

    SessionFactoryOptions setShowSql(boolean showSql);

    boolean isFormatSql();

    SessionFactoryOptions setFormatSql(boolean formatSql);

    boolean isReadonly();

    ZoneId getZoneId();

    /**
     *
     * @return a unmodifiable List
     */
    List<String> getPackagesToScan();

}
