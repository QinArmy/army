package io.army.dialect.mysql;

import java.util.HashSet;
import java.util.Set;

abstract class MySQLDialectUtils {

    private MySQLDialectUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a modifiable set
     * @see <a href="https://dev.mysql.com/doc/refman/8.2/en/keywords.html">Keywords and Reserved Words</a>
     */
    static Set<String> create57KeywordsSet() {
        Set<String> keywords = new HashSet<>();

        keywords.add("ACCESSIBLE");
        keywords.add("ACCOUNT");
        keywords.add("ACTION");
        keywords.add("ADD");
        keywords.add("AFTER");
        keywords.add("AGAINST");
        keywords.add("AGGREGATE");
        keywords.add("ALGORITHM");
        keywords.add("ALL");
        keywords.add("ALTER");
        keywords.add("ALWAYS");
        keywords.add("ANALYSE");
        keywords.add("ANALYZE");
        keywords.add("AND");
        keywords.add("ANY");
        keywords.add("AS");
        keywords.add("ASC");
        keywords.add("ASCII");
        keywords.add("ASENSITIVE");
        keywords.add("AT");
        keywords.add("AUTOEXTEND_SIZE");
        keywords.add("AUTO_INCREMENT");
        keywords.add("AVG");
        keywords.add("AVG_ROW_LENGTH");
        keywords.add("BACKUP");
        keywords.add("BEFORE");
        keywords.add("BEGIN");
        keywords.add("BETWEEN");
        keywords.add("BIGINT");
        keywords.add("BINARY");
        keywords.add("BINLOG");
        keywords.add("BIT");
        keywords.add("BLOB");
        keywords.add("BLOCK");
        keywords.add("BOOL");
        keywords.add("BOOLEAN");
        keywords.add("BOTH");
        keywords.add("BTREE");
        keywords.add("BY");
        keywords.add("BYTE");
        keywords.add("CACHE");
        keywords.add("CALL");
        keywords.add("CASCADE");
        keywords.add("CASCADED");
        keywords.add("CASE");
        keywords.add("CATALOG_NAME");
        keywords.add("CHAIN");
        keywords.add("CHANGE");
        keywords.add("CHANGED");
        keywords.add("CHANNEL");
        keywords.add("CHAR");
        keywords.add("CHARACTER");
        keywords.add("CHARSET");
        keywords.add("CHECK");
        keywords.add("CHECKSUM");
        keywords.add("CIPHER");
        keywords.add("CLASS_ORIGIN");
        keywords.add("CLIENT");
        keywords.add("CLOSE");
        keywords.add("COALESCE");
        keywords.add("CODE");
        keywords.add("COLLATE");
        keywords.add("COLLATION");
        keywords.add("COLUMN");
        keywords.add("COLUMNS");
        keywords.add("COLUMN_FORMAT");
        keywords.add("COLUMN_NAME");
        keywords.add("COMMENT");
        keywords.add("COMMIT");
        keywords.add("COMMITTED");
        keywords.add("COMPACT");
        keywords.add("COMPLETION");
        keywords.add("COMPRESSED");
        keywords.add("COMPRESSION");
        keywords.add("CONCURRENT");
        keywords.add("CONDITION");
        keywords.add("CONNECTION");
        keywords.add("CONSISTENT");
        keywords.add("CONSTRAINT");
        keywords.add("CONSTRAINT_CATALOG");
        keywords.add("CONSTRAINT_NAME");
        keywords.add("CONSTRAINT_SCHEMA");
        keywords.add("CONTAINS");
        keywords.add("CONTEXT");
        keywords.add("CONTINUE");
        keywords.add("CONVERT");
        keywords.add("CPU");
        keywords.add("CREATE");
        keywords.add("CROSS");
        keywords.add("CUBE");
        keywords.add("CURRENT");
        keywords.add("CURRENT_DATE");
        keywords.add("CURRENT_TIME");
        keywords.add("CURRENT_TIMESTAMP");
        keywords.add("CURRENT_USER");
        keywords.add("CURSOR");
        keywords.add("CURSOR_NAME");
        keywords.add("DATA");
        keywords.add("DATABASE");
        keywords.add("DATABASES");
        keywords.add("DATAFILE");
        keywords.add("DATE");
        keywords.add("DATETIME");
        keywords.add("DAY");
        keywords.add("DAY_HOUR");
        keywords.add("DAY_MICROSECOND");
        keywords.add("DAY_MINUTE");
        keywords.add("DAY_SECOND");
        keywords.add("DEALLOCATE");
        keywords.add("DEC");
        keywords.add("DECIMAL");
        keywords.add("DECLARE");
        keywords.add("DEFAULT");
        keywords.add("DEFAULT_AUTH");
        keywords.add("DEFINER");
        keywords.add("DELAYED");
        keywords.add("DELAY_KEY_WRITE");
        keywords.add("DELETE");
        keywords.add("DESC");
        keywords.add("DESCRIBE");
        keywords.add("DES_KEY_FILE");
        keywords.add("DETERMINISTIC");
        keywords.add("DIAGNOSTICS");
        keywords.add("DIRECTORY");
        keywords.add("DISABLE");
        keywords.add("DISCARD");
        keywords.add("DISK");
        keywords.add("DISTINCT");
        keywords.add("DISTINCTROW");
        keywords.add("DIV");
        keywords.add("DO");
        keywords.add("DOUBLE");
        keywords.add("DROP");
        keywords.add("DUAL");
        keywords.add("DUMPFILE");
        keywords.add("DUPLICATE");
        keywords.add("DYNAMIC");
        keywords.add("EACH");
        keywords.add("ELSE");
        keywords.add("ELSEIF");
        keywords.add("ENABLE");
        keywords.add("ENCLOSED");
        keywords.add("ENCRYPTION");
        keywords.add("END");
        keywords.add("ENDS");
        keywords.add("ENGINE");
        keywords.add("ENGINES");
        keywords.add("ENUM");
        keywords.add("ERROR");
        keywords.add("ERRORS");
        keywords.add("ESCAPE");
        keywords.add("ESCAPED");
        keywords.add("EVENT");
        keywords.add("EVENTS");
        keywords.add("EVERY");
        keywords.add("EXCHANGE");
        keywords.add("EXECUTE");
        keywords.add("EXISTS");
        keywords.add("EXIT");
        keywords.add("EXPANSION");
        keywords.add("EXPIRE");
        keywords.add("EXPLAIN");
        keywords.add("EXPORT");
        keywords.add("EXTENDED");
        keywords.add("EXTENT_SIZE");
        keywords.add("FALSE");
        keywords.add("FAST");
        keywords.add("FAULTS");
        keywords.add("FETCH");
        keywords.add("FIELDS");
        keywords.add("FILE");
        keywords.add("FILE_BLOCK_SIZE");
        keywords.add("FILTER");
        keywords.add("FIRST");
        keywords.add("FIXED");
        keywords.add("FLOAT");
        keywords.add("FLOAT4");
        keywords.add("FLOAT8");
        keywords.add("FLUSH");
        keywords.add("FOLLOWS");
        keywords.add("FOR");
        keywords.add("FORCE");
        keywords.add("FOREIGN");
        keywords.add("FORMAT");
        keywords.add("FOUND");
        keywords.add("FROM");
        keywords.add("FULL");
        keywords.add("FULLTEXT");
        keywords.add("FUNCTION");
        keywords.add("GENERAL");
        keywords.add("GENERATED");
        keywords.add("GEOMETRY");
        keywords.add("GEOMETRYCOLLECTION");
        keywords.add("GET");
        keywords.add("GET_FORMAT");
        keywords.add("GLOBAL");
        keywords.add("GRANT");
        keywords.add("GRANTS");
        keywords.add("GROUP");
        keywords.add("GROUP_REPLICATION");
        keywords.add("HANDLER");
        keywords.add("HASH");
        keywords.add("HAVING");
        keywords.add("HELP");
        keywords.add("HIGH_PRIORITY");
        keywords.add("HOST");
        keywords.add("HOSTS");
        keywords.add("HOUR");
        keywords.add("HOUR_MICROSECOND");
        keywords.add("HOUR_MINUTE");
        keywords.add("HOUR_SECOND");
        keywords.add("IDENTIFIED");
        keywords.add("IF");
        keywords.add("IGNORE");
        keywords.add("IGNORE_SERVER_IDS");
        keywords.add("IMPORT");
        keywords.add("IN");
        keywords.add("INDEX");
        keywords.add("INDEXES");
        keywords.add("INFILE");
        keywords.add("INITIAL_SIZE");
        keywords.add("INNER");
        keywords.add("INOUT");
        keywords.add("INSENSITIVE");
        keywords.add("INSERT");
        keywords.add("INSERT_METHOD");
        keywords.add("INSTALL");
        keywords.add("INSTANCE");
        keywords.add("INT");
        keywords.add("INT1");
        keywords.add("INT2");
        keywords.add("INT3");
        keywords.add("INT4");
        keywords.add("INT8");
        keywords.add("INTEGER");
        keywords.add("INTERVAL");
        keywords.add("INTO");
        keywords.add("INVOKER");
        keywords.add("IO");
        keywords.add("IO_AFTER_GTIDS");
        keywords.add("IO_BEFORE_GTIDS");
        keywords.add("IO_THREAD");
        keywords.add("IPC");
        keywords.add("IS");
        keywords.add("ISOLATION");
        keywords.add("ISSUER");
        keywords.add("ITERATE");
        keywords.add("JOIN");
        keywords.add("JSON");
        keywords.add("KEY");
        keywords.add("KEYS");
        keywords.add("KEY_BLOCK_SIZE");
        keywords.add("KILL");
        keywords.add("LANGUAGE");
        keywords.add("LAST");
        keywords.add("LEADING");
        keywords.add("LEAVE");
        keywords.add("LEAVES");
        keywords.add("LEFT");
        keywords.add("LESS");
        keywords.add("LEVEL");
        keywords.add("LIKE");
        keywords.add("LIMIT");
        keywords.add("LINEAR");
        keywords.add("LINES");
        keywords.add("LINESTRING");
        keywords.add("LIST");
        keywords.add("LOAD");
        keywords.add("LOCAL");
        keywords.add("LOCALTIME");
        keywords.add("LOCALTIMESTAMP");
        keywords.add("LOCK");
        keywords.add("LOCKS");
        keywords.add("LOGFILE");
        keywords.add("LOGS");
        keywords.add("LONG");
        keywords.add("LONGBLOB");
        keywords.add("LONGTEXT");
        keywords.add("LOOP");
        keywords.add("LOW_PRIORITY");
        keywords.add("MASTER");
        keywords.add("MASTER_AUTO_POSITION");
        keywords.add("MASTER_BIND");
        keywords.add("MASTER_CONNECT_RETRY");
        keywords.add("MASTER_DELAY");
        keywords.add("MASTER_HEARTBEAT_PERIOD");
        keywords.add("MASTER_HOST");
        keywords.add("MASTER_LOG_FILE");
        keywords.add("MASTER_LOG_POS");
        keywords.add("MASTER_PASSWORD");
        keywords.add("MASTER_PORT");
        keywords.add("MASTER_RETRY_COUNT");
        keywords.add("MASTER_SERVER_ID");
        keywords.add("MASTER_SSL");
        keywords.add("MASTER_SSL_CA");
        keywords.add("MASTER_SSL_CAPATH");
        keywords.add("MASTER_SSL_CERT");
        keywords.add("MASTER_SSL_CIPHER");
        keywords.add("MASTER_SSL_CRL");
        keywords.add("MASTER_SSL_CRLPATH");
        keywords.add("MASTER_SSL_KEY");
        keywords.add("MASTER_SSL_VERIFY_SERVER_CERT");
        keywords.add("MASTER_TLS_VERSION");
        keywords.add("MASTER_USER");
        keywords.add("MATCH");
        keywords.add("MAXVALUE");
        keywords.add("MAX_CONNECTIONS_PER_HOUR");
        keywords.add("MAX_QUERIES_PER_HOUR");
        keywords.add("MAX_ROWS");
        keywords.add("MAX_SIZE");
        keywords.add("MAX_STATEMENT_TIME");
        keywords.add("MAX_UPDATES_PER_HOUR");
        keywords.add("MAX_USER_CONNECTIONS");
        keywords.add("MEDIUM");
        keywords.add("MEDIUMBLOB");
        keywords.add("MEDIUMINT");
        keywords.add("MEDIUMTEXT");
        keywords.add("MEMORY");
        keywords.add("MERGE");
        keywords.add("MESSAGE_TEXT");
        keywords.add("MICROSECOND");
        keywords.add("MIDDLEINT");
        keywords.add("MIGRATE");
        keywords.add("MINUTE");
        keywords.add("MINUTE_MICROSECOND");
        keywords.add("MINUTE_SECOND");
        keywords.add("MIN_ROWS");
        keywords.add("MOD");
        keywords.add("MODE");
        keywords.add("MODIFIES");
        keywords.add("MODIFY");
        keywords.add("MONTH");
        keywords.add("MULTILINESTRING");
        keywords.add("MULTIPOINT");
        keywords.add("MULTIPOLYGON");
        keywords.add("MUTEX");
        keywords.add("MYSQL_ERRNO");
        keywords.add("NAME");
        keywords.add("NAMES");
        keywords.add("NATIONAL");
        keywords.add("NATURAL");
        keywords.add("NCHAR");
        keywords.add("NDB");
        keywords.add("NDBCLUSTER");
        keywords.add("NEVER");
        keywords.add("NEW");
        keywords.add("NEXT");
        keywords.add("NO");
        keywords.add("NODEGROUP");
        keywords.add("NONBLOCKING");
        keywords.add("NONE");
        keywords.add("NOT");
        keywords.add("NO_WAIT");
        keywords.add("NO_WRITE_TO_BINLOG");
        keywords.add("NULL");
        keywords.add("NUMBER");
        keywords.add("NUMERIC");
        keywords.add("NVARCHAR");
        keywords.add("OFFSET");
        keywords.add("OLD_PASSWORD");
        keywords.add("ON");
        keywords.add("ONE");
        keywords.add("ONLY");
        keywords.add("OPEN");
        keywords.add("OPTIMIZE");
        keywords.add("OPTIMIZER_COSTS");
        keywords.add("OPTION");
        keywords.add("OPTIONALLY");
        keywords.add("OPTIONS");
        keywords.add("OR");
        keywords.add("ORDER");
        keywords.add("OUT");
        keywords.add("OUTER");
        keywords.add("OUTFILE");
        keywords.add("OWNER");
        keywords.add("PACK_KEYS");
        keywords.add("PAGE");
        keywords.add("PARSER");
        keywords.add("PARSE_GCOL_EXPR");
        keywords.add("PARTIAL");
        keywords.add("PARTITION");
        keywords.add("PARTITIONING");
        keywords.add("PARTITIONS");
        keywords.add("PASSWORD");
        keywords.add("PHASE");
        keywords.add("PLUGIN");
        keywords.add("PLUGINS");
        keywords.add("PLUGIN_DIR");
        keywords.add("POINT");
        keywords.add("POLYGON");
        keywords.add("PORT");
        keywords.add("PRECEDES");
        keywords.add("PRECISION");
        keywords.add("PREPARE");
        keywords.add("PRESERVE");
        keywords.add("PREV");
        keywords.add("PRIMARY");
        keywords.add("PRIVILEGES");
        keywords.add("PROCEDURE");
        keywords.add("PROCESSLIST");
        keywords.add("PROFILE");
        keywords.add("PROFILES");
        keywords.add("PROXY");
        keywords.add("PURGE");
        keywords.add("QUARTER");
        keywords.add("QUERY");
        keywords.add("QUICK");
        keywords.add("RANGE");
        keywords.add("READ");
        keywords.add("READS");
        keywords.add("READ_ONLY");
        keywords.add("READ_WRITE");
        keywords.add("REAL");
        keywords.add("REBUILD");
        keywords.add("RECOVER");
        keywords.add("REDOFILE");
        keywords.add("REDO_BUFFER_SIZE");
        keywords.add("REDUNDANT");
        keywords.add("REFERENCES");
        keywords.add("REGEXP");
        keywords.add("RELAY");
        keywords.add("RELAYLOG");
        keywords.add("RELAY_LOG_FILE");
        keywords.add("RELAY_LOG_POS");
        keywords.add("RELAY_THREAD");
        keywords.add("RELEASE");
        keywords.add("RELOAD");
        keywords.add("REMOVE");
        keywords.add("RENAME");
        keywords.add("REORGANIZE");
        keywords.add("REPAIR");
        keywords.add("REPEAT");
        keywords.add("REPEATABLE");
        keywords.add("REPLACE");
        keywords.add("REPLICATE_DO_DB");
        keywords.add("REPLICATE_DO_TABLE");
        keywords.add("REPLICATE_IGNORE_DB");
        keywords.add("REPLICATE_IGNORE_TABLE");
        keywords.add("REPLICATE_REWRITE_DB");
        keywords.add("REPLICATE_WILD_DO_TABLE");
        keywords.add("REPLICATE_WILD_IGNORE_TABLE");
        keywords.add("REPLICATION");
        keywords.add("REQUIRE");
        keywords.add("RESET");
        keywords.add("RESIGNAL");
        keywords.add("RESTORE");
        keywords.add("RESTRICT");
        keywords.add("RESUME");
        keywords.add("RETURN");
        keywords.add("RETURNED_SQLSTATE");
        keywords.add("RETURNS");
        keywords.add("REVERSE");
        keywords.add("REVOKE");
        keywords.add("RIGHT");
        keywords.add("RLIKE");
        keywords.add("ROLLBACK");
        keywords.add("ROLLUP");
        keywords.add("ROTATE");
        keywords.add("ROUTINE");
        keywords.add("ROW");
        keywords.add("ROWS");
        keywords.add("ROW_COUNT");
        keywords.add("ROW_FORMAT");
        keywords.add("RTREE");
        keywords.add("SAVEPOINT");
        keywords.add("SCHEDULE");
        keywords.add("SCHEMA");
        keywords.add("SCHEMAS");
        keywords.add("SCHEMA_NAME");
        keywords.add("SECOND");
        keywords.add("SECOND_MICROSECOND");
        keywords.add("SECURITY");
        keywords.add("SELECT");
        keywords.add("SENSITIVE");
        keywords.add("SEPARATOR");
        keywords.add("SERIAL");
        keywords.add("SERIALIZABLE");
        keywords.add("SERVER");
        keywords.add("SESSION");
        keywords.add("SET");
        keywords.add("SHARE");
        keywords.add("SHOW");
        keywords.add("SHUTDOWN");
        keywords.add("SIGNAL");
        keywords.add("SIGNED");
        keywords.add("SIMPLE");
        keywords.add("SLAVE");
        keywords.add("SLOW");
        keywords.add("SMALLINT");
        keywords.add("SNAPSHOT");
        keywords.add("SOCKET");
        keywords.add("SOME");
        keywords.add("SONAME");
        keywords.add("SOUNDS");
        keywords.add("SOURCE");
        keywords.add("SPATIAL");
        keywords.add("SPECIFIC");
        keywords.add("SQL");
        keywords.add("SQLEXCEPTION");
        keywords.add("SQLSTATE");
        keywords.add("SQLWARNING");
        keywords.add("SQL_AFTER_GTIDS");
        keywords.add("SQL_AFTER_MTS_GAPS");
        keywords.add("SQL_BEFORE_GTIDS");
        keywords.add("SQL_BIG_RESULT");
        keywords.add("SQL_BUFFER_RESULT");
        keywords.add("SQL_CACHE");
        keywords.add("SQL_CALC_FOUND_ROWS");
        keywords.add("SQL_NO_CACHE");
        keywords.add("SQL_SMALL_RESULT");
        keywords.add("SQL_THREAD");
        keywords.add("SQL_TSI_DAY");
        keywords.add("SQL_TSI_HOUR");
        keywords.add("SQL_TSI_MINUTE");
        keywords.add("SQL_TSI_MONTH");
        keywords.add("SQL_TSI_QUARTER");
        keywords.add("SQL_TSI_SECOND");
        keywords.add("SQL_TSI_WEEK");
        keywords.add("SQL_TSI_YEAR");
        keywords.add("SSL");
        keywords.add("STACKED");
        keywords.add("START");
        keywords.add("STARTING");
        keywords.add("STARTS");
        keywords.add("STATS_AUTO_RECALC");
        keywords.add("STATS_PERSISTENT");
        keywords.add("STATS_SAMPLE_PAGES");
        keywords.add("STATUS");
        keywords.add("STOP");
        keywords.add("STORAGE");
        keywords.add("STORED");
        keywords.add("STRAIGHT_JOIN");
        keywords.add("STRING");
        keywords.add("SUBCLASS_ORIGIN");
        keywords.add("SUBJECT");
        keywords.add("SUBPARTITION");
        keywords.add("SUBPARTITIONS");
        keywords.add("SUPER");
        keywords.add("SUSPEND");
        keywords.add("SWAPS");
        keywords.add("SWITCHES");
        keywords.add("TABLE");
        keywords.add("TABLES");
        keywords.add("TABLESPACE");
        keywords.add("TABLE_CHECKSUM");
        keywords.add("TABLE_NAME");
        keywords.add("TEMPORARY");
        keywords.add("TEMPTABLE");
        keywords.add("TERMINATED");
        keywords.add("TEXT");
        keywords.add("THAN");
        keywords.add("THEN");
        keywords.add("TIME");
        keywords.add("TIMESTAMP");
        keywords.add("TIMESTAMPADD");
        keywords.add("TIMESTAMPDIFF");
        keywords.add("TINYBLOB");
        keywords.add("TINYINT");
        keywords.add("TINYTEXT");
        keywords.add("TO");
        keywords.add("TRAILING");
        keywords.add("TRANSACTION");
        keywords.add("TRIGGER");
        keywords.add("TRIGGERS");
        keywords.add("TRUE");
        keywords.add("TRUNCATE");
        keywords.add("TYPE");
        keywords.add("TYPES");
        keywords.add("UNCOMMITTED");
        keywords.add("UNDEFINED");
        keywords.add("UNDO");
        keywords.add("UNDOFILE");
        keywords.add("UNDO_BUFFER_SIZE");
        keywords.add("UNICODE");
        keywords.add("UNINSTALL");
        keywords.add("UNION");
        keywords.add("UNIQUE");
        keywords.add("UNKNOWN");
        keywords.add("UNLOCK");
        keywords.add("UNSIGNED");
        keywords.add("UNTIL");
        keywords.add("UPDATE");
        keywords.add("UPGRADE");
        keywords.add("USAGE");
        keywords.add("USE");
        keywords.add("USER");
        keywords.add("USER_RESOURCES");
        keywords.add("USE_FRM");
        keywords.add("USING");
        keywords.add("UTC_DATE");
        keywords.add("UTC_TIME");
        keywords.add("UTC_TIMESTAMP");
        keywords.add("VALIDATION");
        keywords.add("VALUE");
        keywords.add("VALUES");
        keywords.add("VARBINARY");
        keywords.add("VARCHAR");
        keywords.add("VARCHARACTER");
        keywords.add("VARIABLES");
        keywords.add("VARYING");
        keywords.add("VIEW");
        keywords.add("VIRTUAL");
        keywords.add("WAIT");
        keywords.add("WARNINGS");
        keywords.add("WEEK");
        keywords.add("WEIGHT_STRING");
        keywords.add("WHEN");
        keywords.add("WHERE");
        keywords.add("WHILE");
        keywords.add("WITH");
        keywords.add("WITHOUT");
        keywords.add("WORK");
        keywords.add("WRAPPER");
        keywords.add("WRITE");
        keywords.add("X509");
        keywords.add("XA");
        keywords.add("XID");
        keywords.add("XML");
        keywords.add("XOR");
        keywords.add("YEAR");
        keywords.add("YEAR_MONTH");
        keywords.add("ZEROFIL");

        return keywords;
    }

    /**
     * @return a modifiable set
     * @see <a href="https://dev.mysql.com/doc/refman/8.2/en/keywords.html">Keywords and Reserved Words</a>
     */
    static Set<String> create80KeywordsSet() {
        Set<String> keywords = create57KeywordsSet();

        keywords.add("ACTIVE");
        keywords.add("ADMIN");
        keywords.add("ARRAY");
        keywords.add("BUCKETS");
        keywords.add("CLONE");
        keywords.add("COMPONENT");
        keywords.add("CUME_DIST");
        keywords.add("DEFINITION");
        keywords.add("DENSE_RANK");
        keywords.add("DESCRIPTION");
        keywords.add("EMPTY");
        keywords.add("ENFORCED");
        keywords.add("EXCEPT");
        keywords.add("EXCLUDE");
        keywords.add("FAILED_LOGIN_ATTEMPTS");
        keywords.add("FIRST_VALUE");
        keywords.add("FOLLOWING");
        keywords.add("GEOMCOLLECTION");
        keywords.add("GET_MASTER_PUBLIC_KEY");
        keywords.add("GROUPING");
        keywords.add("GROUPS");
        keywords.add("HISTOGRAM");
        keywords.add("HISTORY");
        keywords.add("INACTIVE");
        keywords.add("INVISIBLE");
        keywords.add("JSON_TABLE");
        keywords.add("LAG");
        keywords.add("LAST_VALUE");
        keywords.add("LATERAL");
        keywords.add("LEAD");
        keywords.add("LOCKED");
        keywords.add("MASTER_COMPRESSION_ALGORITHMS");
        keywords.add("MASTER_PUBLIC_KEY_PATH");
        keywords.add("MASTER_TLS_CIPHERSUITES");
        keywords.add("MASTER_ZSTD_COMPRESSION_LEVEL");
        keywords.add("MEMBER");
        keywords.add("NESTED");
        keywords.add("NETWORK_NAMESPACE");
        keywords.add("NOWAIT");
        keywords.add("NTH_VALUE");
        keywords.add("NTILE");
        keywords.add("NULLS");
        keywords.add("OF");
        keywords.add("OFF");
        keywords.add("OJ");
        keywords.add("OLD");
        keywords.add("OPTIONAL");
        keywords.add("ORDINALITY");
        keywords.add("ORGANIZATION");
        keywords.add("OTHERS");
        keywords.add("OVER");
        keywords.add("PASSWORD_LOCK_TIME");
        keywords.add("PATH");
        keywords.add("PERCENT_RANK");
        keywords.add("PERSIST");
        keywords.add("PERSIST_ONLY");
        keywords.add("PRECEDING");
        keywords.add("PRIVILEGE_CHECKS_USER");
        keywords.add("PROCESS");
        keywords.add("RANDOM");
        keywords.add("RANK");
        keywords.add("RECURSIVE");
        keywords.add("REFERENCE");
        keywords.add("REQUIRE_ROW_FORMAT");
        keywords.add("RESOURCE");
        keywords.add("RESPECT");
        keywords.add("RESTART");
        keywords.add("RETAIN");
        keywords.add("REUSE");
        keywords.add("ROLE");
        keywords.add("ROW_NUMBER");
        keywords.add("SECONDARY");
        keywords.add("SECONDARY_ENGINE");
        keywords.add("SECONDARY_LOAD");
        keywords.add("SECONDARY_UNLOAD");
        keywords.add("SKIP");
        keywords.add("SRID");
        keywords.add("STREAM");
        keywords.add("SYSTEM");
        keywords.add("THREAD_PRIORITY");
        keywords.add("TIES");
        keywords.add("UNBOUNDED");
        keywords.add("VCPU");
        keywords.add("VISIBLE");
        keywords.add("WINDOW");

        // 8.0 removed keywords
        createMySQL80RemovedKeywords(keywords);
        return keywords;
    }


    /*################################## blow private method ##################################*/

    /**
     * MySQL 8.0 Removed Keywords then Reserved Words
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.2/en/keywords.html">Keywords and Reserved Words</a>
     */
    private static void createMySQL80RemovedKeywords(Set<String> keywords) {

        keywords.remove("ANALYSE");
        keywords.remove("DES_KEY_FILE");
        keywords.remove("PARSE_GCOL_EXPR");
        keywords.remove("REDOFILE");

        keywords.remove("SQL_CACHE");
    }


}
