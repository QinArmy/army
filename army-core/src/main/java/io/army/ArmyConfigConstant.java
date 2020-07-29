package io.army;

import io.army.generator.snowflake.FiveBitWorkerSnowflake;
import io.army.generator.snowflake.Snowflake;

public interface ArmyConfigConstant {

    String ZONE_ID = "army.%s.zoneId";

    String PACKAGE_TO_SCAN = "army.%s.packageToScan";

    String SHARDING_MODE = "army.%s.shardingMode";

    String READ_ONLY = "army.%s.readOnly";

    String CURRENT_SESSION_CONTEXT_CLASS = "army.%s.currentSessionContextClass";

    String SQL_DIALECT = "army.%s.sqlDialect";

    String SHOW_SQL = "army.%s.showSQL";

    String FORMAT_SQL = "army.%s.formatSQL";

    /**
     * value is {@code true} or {@code false}
     */
    String MIGRATION_META = "army.%s.migration.meta";

    /**
     * @see io.army.generator.snowflake.SingleApplicationSnowflakeClient
     */
    String DATA_CENTER_FORMAT = "army.single.%s.dataCenterId";

    /**
     * @see io.army.generator.snowflake.SingleApplicationSnowflakeClient
     */
    String WORKER_FORMAT = "army.single.%s.workerId";

    /**
     * (optional) Specifies the implementation class name of {@link Snowflake}
     * , if absent the default is {@link FiveBitWorkerSnowflake}.
     */
    String SNOWFLAKE_CLASS = "army.snowflake.class";

    String SNOWFLAKE_CLIENT_NAME = "army.snowflake.client";

    String SNOWFLAKE_DEFAULT_TIME = "army.snowflake.time";

    String SESSION_CACHE = "army.%s.session.cache";

    String SHARDING_SUB_QUERY_INSERT = "army.%s.sharding.sub.query.insert.enable";

    String ALLOW_SPAN_SHARDING = "army.%s.allowSpanSharding";

}
