package io.army;

import io.army.util.ArrayUtils;

import java.time.ZoneId;

class SessionFactoryOptionsImpl implements SessionFactoryOptions {

    private final boolean readonly;

    private final ZoneId zoneId;

    private final String[] packagesToScan;

    private boolean showSql;

    private boolean formatSql;


    SessionFactoryOptionsImpl(boolean readonly, ZoneId zoneId,String[] packagesToScan) {
        this.readonly = readonly;
        this.zoneId = zoneId;
        if(packagesToScan == null){
            this.packagesToScan = ArrayUtils.EMPTY_STRING_ARRAY;
        }else {
            this.packagesToScan = packagesToScan;
        }

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
    public String[] getPackagesToScan() {
        return packagesToScan;
    }
}
