package io.army.boot;

import io.army.SessionFactoryOptions;
import io.army.util.ArrayUtils;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

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
    public SessionFactoryOptions showSql(boolean showSql) {
         this.showSql = showSql;
         return this;
    }

    @Override
    public boolean isFormatSql() {
        return formatSql;
    }

    @Override
    public SessionFactoryOptions formatSql(boolean formatSql) {
        this.formatSql = formatSql;
        return this;
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public ZoneId zoneId() {
        return zoneId;
    }

    @Override
    public List<String> packagesToScan() {
        return packagesToScan;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SessionFactoryOptionsImpl.class.getSimpleName() + "[", "]")
                .add("readonly=" + readonly)
                .add("zoneId=" + zoneId)
                .add("packagesToScan=" + packagesToScan)
                .add("showSql=" + showSql)
                .add("formatSql=" + formatSql)
                .toString();
    }
}
