package io.army;

import io.army.util.Assert;

import javax.sql.DataSource;
import java.time.ZoneId;

class SessionFactoryBuilderImpl implements SessionFactoryBuilder {

    private ZoneId zoneId;

    private boolean showSql;

    private boolean formatSql;

    private boolean readonly;

    private String[] packagesToScan;

    private DataSource dataSource;


    @Override
    public SessionFactory build() {
        ZoneId factoryZoneId = zoneId;
        if (zoneId == null) {
            zoneId = ZoneId.systemDefault();
        }
        SessionFactoryOptions options = new SessionFactoryOptionsImpl(readonly, factoryZoneId,packagesToScan)
                .setShowSql(showSql)
                .setFormatSql(formatSql);

        Assert.notNull(dataSource,"dataSource required");

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl(options,dataSource);
        sessionFactory.initSessionFactory();
        return sessionFactory;
    }



    @Override
    public ZoneId getZoneId() {
        return zoneId;
    }

    @Override
    public SessionFactoryBuilderImpl setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
        return this;
    }

    @Override
    public boolean isShowSql() {
        return showSql;
    }

    @Override
    public SessionFactoryBuilderImpl setShowSql(boolean showSql) {
        this.showSql = showSql;
        return this;
    }

    @Override
    public boolean isFormatSql() {
        return formatSql;
    }

    @Override
    public SessionFactoryBuilderImpl setFormatSql(boolean formatSql) {
        this.formatSql = formatSql;
        return this;
    }

    public boolean isReadonly() {
        return readonly;
    }

    public SessionFactoryBuilderImpl setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }


    @Override
    public SessionFactoryBuilderImpl setPackagesToScan(String[] packagesToScan) {
        this.packagesToScan = packagesToScan;
        return this;
    }

    @Override
    public String[] getPackagesToScan() {
        return this.packagesToScan;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
