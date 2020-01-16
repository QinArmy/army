package io.army;

import java.time.ZoneId;

public interface SessionFactoryOptions {

    boolean isShowSql();

    SessionFactoryOptions setShowSql(boolean showSql);

    boolean isFormatSql();

    SessionFactoryOptions setFormatSql(boolean formatSql);

    boolean isReadonly();

    ZoneId getZoneId();

    String[] getPackagesToScan();

}
