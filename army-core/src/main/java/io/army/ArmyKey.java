package io.army;

@Deprecated
public interface ArmyKey {

    String SYNC_EXECUTOR_PROVIDER = "army.%s.sync.executorProvider";

    String CATALOG = "army.%s.catalog";

    String SCHEMA = "army.%s.schema";

    String ZONE_ID = "army.%s.zoneId";

    String PACKAGE_TO_SCAN = "army.%s.packageToScan";

    String READ_ONLY = "army.{0}.readOnly";


    String DATABASE = "army.%s.database";

    String SHOW_SQL = "army.%s.showSQL";

    String FORMAT_SQL = "army.%s.formatSQL";

    /**
     * value is {@code true} or {@code false}
     */
    String MIGRATION_MODE = "army.%s.migration.mode";


    /**
     * @see io.army.generator.snowflake.SingleApplicationSnowflakeClient
     */
    String DATA_CENTER_FORMAT = "army.single.%s.dataCenterId";

    /**
     * @see io.army.generator.snowflake.SingleApplicationSnowflakeClient
     */
    String WORKER_FORMAT = "army.single.%s.workerId";

    /**
     * (optional) Specifies the implementation class name of {@link _Snowflake}
     * , if absent the default is {@link FiveBitWorkerSnowflake}.
     */
    String SNOWFLAKE_CLASS = "army.snowflake.class";

    String SNOWFLAKE_CLIENT_NAME = "army.snowflake.client.name";

    String SNOWFLAKE_DEFAULT_TIME = "army.snowflake.time";

    String SESSION_CACHE = "army.%s.session.cache";

    String SHARDING_SUB_QUERY_INSERT = "army.%s.sharding.sub.query.insert.enable";

    String ALLOW_SPAN_SHARDING = "army.%s.allowSpanSharding";

    String COMPARE_DEFAULT_ON_MIGRATING = "army.%s.compareDefaultOnMigrating";

    String SINGLE_APPLICATION = "army.%s.single.application";
}
