package io.army.boot;

import io.army.SessionFactoryOptions;
import io.army.util.ArrayUtils;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SessionFactoryOptionsImpl implements SessionFactoryOptions {

    private final boolean readonly;

    private final ZoneId zoneId;

    private final List<String> packagesToScan;

    private boolean showSql;

    private boolean formatSql;


    SessionFactoryOptionsImpl(boolean readonly, ZoneId zoneId,List<String> packagesToScan) {
        this.readonly = readonly;
        this.zoneId = zoneId;
        this.packagesToScan = ArrayUtils.asUnmodifiableList(packagesToScan);
    }

    @Override
    public boolean isShowSql() {
        return showSql;
    }

    @Override
    public SessionFactoryOptions setShowSql(boolean showSql) {
         this.showSql = showSql;
         return this;
    }

    @Override
    public boolean isFormatSql() {
        return formatSql;
    }

    @Override
    public SessionFactoryOptions setFormatSql(boolean formatSql) {
        this.formatSql = formatSql;
        return this;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public List<String> getPackagesToScan() {
        return packagesToScan;
    }
}
