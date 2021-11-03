package io.army;

import io.army.asm.TableMetaLoader;
import io.army.codec.FieldCodec;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.criteria.impl.TableMetaFactory;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.dialect.UnsupportedDatabaseException;
import io.army.env.ArmyEnvironment;
import io.army.generator.FieldGenerator;
import io.army.generator.GeneratorFactory;
import io.army.generator.PreFieldGenerator;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.sharding.RouteMetaData;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;

/**
 * @see GenericSessionFactory
 */
public abstract class GenericSessionFactoryUtils {


    protected GenericSessionFactoryUtils() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    protected static Database readDatabase(GenericSessionFactory sessionFactory) {
        String key = String.format(ArmyConfigConstant.DATABASE, sessionFactory.name());
        return sessionFactory.environment().getProperty(key, Database.class);
    }


    protected static Database convertToDatabase(String databaseProductName, int majorVersion, int minorVersion) {
        Database database;
        switch (databaseProductName) {
            case "MySQL":
                database = convertToMySQLDatabase(databaseProductName, majorVersion, minorVersion);
                break;
            case "Oracle":
                database = convertOracleDatabase(databaseProductName, majorVersion, minorVersion);
                break;
            case "PostgreSQL":
                database = convertPostgreDatabase(databaseProductName, majorVersion, minorVersion);
                break;
            default:
                throw new UnsupportedDatabaseException(databaseProductName, majorVersion, minorVersion);
        }
        return database;
    }


    protected static Dialect createDialect(@Nullable Database database, Database extractedDatabase
            , GenericRmSessionFactory sessionFactory) {

        Database actualDatabase = decideActualDatabase(database, extractedDatabase);
        Dialect dialect;
//        switch (actualDatabase.family()) {
//            case MySQL:
//                dialect = MySQLDialectFactory.createMySQLDialect(actualDatabase, sessionFactory);
//                break;
//            case Postgre:
//                dialect = PostgreDialectFactory.createPostgreDialect(actualDatabase, sessionFactory);
//                break;
//            //  case Db2:
//            case Oracle:
//                // case SQL_Server:
//            default:
//                throw new RuntimeException(String.format("unknown Database[%s]", actualDatabase));
//        }
//        return dialect;
        return null;
    }


    protected static Database decideActualDatabase(@Nullable Database database, Database extractedDatabase) {
//        Database actual = database;
//        if (actual == null) {
//            actual = extractedDatabase;
//        } else if (!extractedDatabase.compatible(database)) {
//            throw new DialectNotMatchException(ErrorCode.META_ERROR, "Database[%s] and extract database[%s] not match."
//                    , database, extractedDatabase);
//        }
        return null;
    }


    static Function<Throwable, Throwable> createComposedExceptionFunction(
            GenericFactoryBuilderImpl<?> builder) {

        final Function<RuntimeException, RuntimeException> customFunction = builder.exceptionFunction();
        final Function<RuntimeException, RuntimeException> springFunction = builder.springExceptionFunction();

        return throwable -> convertToCustomException(throwable, springFunction, customFunction);
    }

    static ShardingMode shardingMode(GenericFactoryBuilderImpl<?> builder) {
        ShardingMode shardingMode = builder.shardingMode();
        if (shardingMode == null) {
            throw new SessionFactoryException("shardingMode required");
        }
        return shardingMode;
    }


    static final class GeneratorWrapper {

        private final Map<FieldMeta<?, ?>, FieldGenerator> generatorChain;

        private final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

        public GeneratorWrapper(Map<FieldMeta<?, ?>, FieldGenerator> generatorChain
                , Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain) {
            this.generatorChain = Collections.unmodifiableMap(generatorChain);
            this.tableGeneratorChain = Collections.unmodifiableMap(tableGeneratorChain);
        }

        /**
         * @return a modifiable map
         */
        public Map<FieldMeta<?, ?>, FieldGenerator> getGeneratorChain() {
            return generatorChain;
        }

        /**
         * @return a modifiable map
         */
        public Map<TableMeta<?>, List<FieldMeta<?, ?>>> getTableGeneratorChain() {
            return tableGeneratorChain;
        }
    }

    static boolean sessionCache(ArmyEnvironment env, String factoryName) {
        return env.getProperty(String.format(ArmyConfigConstant.SESSION_CACHE, factoryName)
                , Boolean.class, Boolean.TRUE);
    }

    protected static void assertTableCountOfSharding(final int tableCountOfSharding, GenericSessionFactory sessionFactory) {
        switch (sessionFactory.shardingMode()) {
            case NO_SHARDING:
                if (tableCountOfSharding != 1) {
                    throw new SessionFactoryException("%s tableCountOfSharding must equals 1 in NO_SHARDING mode.", sessionFactory);
                }
                break;
            case SINGLE_DATABASE_SHARDING:
            case SHARDING:
                if (tableCountOfSharding < 1) {
                    throw new SessionFactoryException("%s tableCountOfSharding must great than 0 in SHARDING mode.", sessionFactory);
                }
            default:
                throw new IllegalArgumentException(String.format("not support %s", sessionFactory.shardingMode()));
        }
    }

    static boolean shardingSubQueryInsert(ArmyEnvironment env, String factoryName, ShardingMode shardingMode) {
        boolean support;
        if (shardingMode == ShardingMode.NO_SHARDING) {
            support = false;
        } else {
            support = env.getProperty(String.format(ArmyConfigConstant.SHARDING_SUB_QUERY_INSERT, factoryName)
                    , Boolean.class, Boolean.FALSE);
        }
        return support;
    }

    static boolean allowSpanSharding(ArmyEnvironment env, String factoryName, ShardingMode shardingMode) {
        boolean allow;
        if (shardingMode == ShardingMode.NO_SHARDING) {
            allow = false;
        } else {
            allow = env.getProperty(String.format(ArmyConfigConstant.ALLOW_SPAN_SHARDING, factoryName)
                    , Boolean.class, Boolean.FALSE);
        }
        return allow;
    }

    static boolean compareDefaultOnMigrating(ArmyEnvironment env, String factoryName) {
        return env.getProperty(String.format(ArmyConfigConstant.COMPARE_DEFAULT_ON_MIGRATING, factoryName)
                , Boolean.class, Boolean.FALSE);
    }

    static Map<Class<?>, TableMeta<?>> scanPackagesForMeta(SchemaMeta schemaMeta, String factoryName, ArmyEnvironment env) {
        List<String> packagesToScan = env.getRequiredPropertyList(
                String.format(ArmyConfigConstant.PACKAGE_TO_SCAN, factoryName), String[].class);
        return TableMetaLoader.build()
                .scanTableMeta(schemaMeta, packagesToScan);
    }


    static GeneratorWrapper createGeneratorWrapper(Collection<TableMeta<?>> tableMetas
            , GenericSessionFactory sessionFactory) {

        final Map<FieldMeta<?, ?>, FieldGenerator> generatorMap = new HashMap<>();
        final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain = new HashMap<>();

        for (TableMeta<?> tableMeta : tableMetas) {
            Map<String, GeneratorMeta> propGeneratorMap = new HashMap<>();

            for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
                GeneratorMeta generatorMeta = fieldMeta.generator();
                if (generatorMeta == null) {
                    continue;
                }
                assertPreGenerator(generatorMeta);

                FieldGenerator generator = GeneratorFactory.getGenerator(fieldMeta, sessionFactory);
                // create generator
                generatorMap.put(fieldMeta, generator);

                if (generator instanceof PreFieldGenerator) {
                    propGeneratorMap.put(fieldMeta.propertyName(), generatorMeta);
                }
            }

            if (!propGeneratorMap.isEmpty()) {
                List<FieldMeta<?, ?>> chain = createTablePreGeneratorChain(tableMeta, propGeneratorMap);
                tableGeneratorChain.put(tableMeta, chain);
            }

        }
        return new GeneratorWrapper(generatorMap, tableGeneratorChain);
    }

    static SchemaMeta obtainSchemaMeta(String factoryName, ArmyEnvironment env) {
        String catalog = env.getProperty(String.format(ArmyConfigConstant.CATALOG, factoryName), "");
        String schema = env.getProperty(String.format(ArmyConfigConstant.SCHEMA, factoryName), "");
        return SchemaMetaFactory.getSchema(catalog, schema);
    }

    static ZoneId createZoneId(ArmyEnvironment env, String factoryName) {
        String zoneIdText = env.getProperty(String.format(ArmyConfigConstant.ZONE_ID, factoryName));
        ZoneId zoneId;
        if (StringUtils.hasText(zoneIdText)) {
            zoneId = ZoneId.of(zoneIdText);
        } else {
            zoneId = ZoneId.systemDefault();
        }
        return zoneId;
    }

    static boolean readOnly(String factoryName, ArmyEnvironment env) {
        return env.getProperty(String.format(ArmyConfigConstant.READ_ONLY, factoryName), Boolean.class, Boolean.FALSE);
    }

    static Map<FieldMeta<?, ?>, FieldCodec> createTableFieldCodecMap(
            @Nullable Collection<FieldCodec> fieldCodecs) {

        if (CollectionUtils.isEmpty(fieldCodecs)) {
            return Collections.emptyMap();
        }

        Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap = new HashMap<>();

        for (FieldCodec codec : fieldCodecs) {

            for (FieldMeta<?, ?> fieldMeta : codec.fieldMetaSet()) {
                if (!fieldMeta.codec()) {
                    throw new SessionFactoryException("FieldMeta[%s] don't support FieldCodec.", fieldMeta);
                }
                if (fieldCodecMap.putIfAbsent(fieldMeta, codec) != null) {
                    throw new SessionFactoryException("FieldMeta[%s]'s FieldCodec[%s] duplication.", fieldMeta, codec);
                }
            }
        }
        // check all codec field have FieldCodec
        Set<FieldMeta<?, ?>> codecFieldSet = new HashSet<>(fieldCodecMap.keySet());
        codecFieldSet.removeAll(TableMetaFactory.codecFieldMetaSet());
        if (!codecFieldSet.isEmpty()) {
            throw new SessionFactoryException("FieldMeta set [%s] not found FieldCodec.", codecFieldSet);
        }
        return Collections.unmodifiableMap(fieldCodecMap);
    }


    protected static final class RouteMetaDataImpl implements RouteMetaData {

        private final ShardingMode shardingMode;

        private final int databaseCount;

        private final int tableContPerDatabase;

        public RouteMetaDataImpl(ShardingMode shardingMode, int databaseCount, int tableContPerDatabase) {
            this.shardingMode = shardingMode;
            this.databaseCount = databaseCount;
            this.tableContPerDatabase = tableContPerDatabase;
        }

        @Override
        public ShardingMode shardingMode() {
            return this.shardingMode;
        }

        @Override
        public int databaseCount() {
            return this.databaseCount;
        }

        @Override
        public int tableContPerDatabase() {
            return this.tableContPerDatabase;
        }
    }



    /*################################## blow private method ##################################*/


    /**
     * @param thisGeneratorMap a modifiable map
     * @return a unmodifiable list
     * @see PreFieldGenerator
     */
    private static List<FieldMeta<?, ?>> createTablePreGeneratorChain(TableMeta<?> tableMeta
            , Map<String, GeneratorMeta> thisGeneratorMap) {

        if (tableMeta.parentMeta() != null) {
            // appendText parentMeta generator
            appendPrentTableGeneratorMap(tableMeta, thisGeneratorMap);
        }

        final List<Set<String>> dependLevelList = new ArrayList<>();
        final Set<String> ancestorLevelSet = new HashSet<>();

        //1. add ancestor level set, they have no dependence.
        for (Map.Entry<String, GeneratorMeta> e : thisGeneratorMap.entrySet()) {
            if (!StringUtils.hasText(e.getValue().dependPropName())) {
                ancestorLevelSet.add(e.getKey());
            }
        }
        dependLevelList.add(ancestorLevelSet);

        //2. add child level after ancestor level
        appendChildLevel(dependLevelList, Collections.unmodifiableMap(thisGeneratorMap));

        //3.  create chain
        List<FieldMeta<?, ?>> tempChain = new ArrayList<>(), chain;
        for (Set<String> dependLevel : dependLevelList) {
            for (String propName : dependLevel) {
                tempChain.add(tableMeta.getField(propName));
            }
        }
        // 4. adjust list's initialCapacity
        chain = new ArrayList<>(tempChain.size());
        chain.addAll(tempChain);

        return Collections.unmodifiableList(chain);
    }

    /**
     * @param thisGeneratorMap a modifiable map
     * @see PreFieldGenerator
     */
    private static void appendPrentTableGeneratorMap(TableMeta<?> tableMeta
            , Map<String, GeneratorMeta> thisGeneratorMap) {
        TableMeta<?> parentMeta = tableMeta.parentMeta();
        Assert.isTrue(parentMeta != null, "entity no parentMeta");

        Assert.isTrue(!thisGeneratorMap.containsKey(TableMeta.ID)
                , () -> String.format("child entity[%s] cannot have id generator.", tableMeta.javaType().getName()));

        for (FieldMeta<?, ?> fieldMeta : parentMeta.fieldCollection()) {
            GeneratorMeta generatorMeta = fieldMeta.generator();
            if (generatorMeta == null) {
                continue;
            }
            if (thisGeneratorMap.containsKey(fieldMeta.propertyName())) {
                throw new MetaException(ErrorCode.META_ERROR, "entity[%s] prop[%s] couldn'field override parentMeta's generator"
                        , tableMeta.javaType().getName(), fieldMeta.fieldName());
            }

            thisGeneratorMap.put(fieldMeta.propertyName(), generatorMeta);
        }
    }

    /**
     * @param dependLevelList  a modifiable list
     * @param propGeneratorMap a unmodifiable map
     */
    private static void appendChildLevel(List<Set<String>> dependLevelList
            , Map<String, GeneratorMeta> propGeneratorMap) {
        final Set<String> lastLevelPropSet = dependLevelList.get(dependLevelList.size() - 1);

        final Set<String> thisLevelSet = new HashSet<>();
        int thisLevelCount = 0;
        for (Map.Entry<String, GeneratorMeta> e : propGeneratorMap.entrySet()) {
            if (lastLevelPropSet.contains(e.getValue().dependPropName())) {
                thisLevelSet.add(e.getKey());
                thisLevelCount++;
            }
        }
        if (thisLevelCount > 0) {
            dependLevelList.add(thisLevelSet);
            // continue appendText then level
            appendChildLevel(dependLevelList, propGeneratorMap);
        }
    }

    private static MetaException createDependException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException(ErrorCode.META_ERROR, "Entity[%s] propName[%s] generator depend error"
                , fieldMeta.tableMeta().javaType().getName()
                , fieldMeta.propertyName());
    }

    private static void assertPreGenerator(GeneratorMeta generatorMeta) {
        if (StringUtils.hasText(generatorMeta.dependPropName())) {
            TableMeta<?> tableMeta = generatorMeta.fieldMeta().tableMeta();

            if (!tableMeta.mappingProp(generatorMeta.dependPropName())) {
                TableMeta<?> parentMeta = tableMeta.parentMeta();
                if (parentMeta == null || !parentMeta.mappingProp(generatorMeta.dependPropName())) {
                    throw createDependException(generatorMeta.fieldMeta());
                }
            }

        }
    }

    private static Database convertOracleDatabase(String productName, int major, int minor) {
        throw new UnsupportedDatabaseException(productName, major, minor);
    }

    private static Database convertPostgreDatabase(String productName, int major, int minor) {
//        Database database;
//        switch (major) {
//            case 11:
//                database = Database.Postgre11;
//                break;
//            case 12:
//                database = Database.Postgre12;
//                break;
//            default:
//                throw new UnsupportedDatabaseException(productName, major, minor);
//        }
        return null;

    }


    private static Database convertToMySQLDatabase(String minorVersion, int major, int minor) {
        Database database = null;
        switch (major) {
            case 5:
                if (minor < 7) {
                    throw new UnsupportedDatabaseException(minorVersion, major, minor);
                }
                // database = Database.MySQL57;
                break;
            case 8:
                if (minor == 0) {
                    // database = Database.MySQL80;
                } else {
                    throw new UnsupportedDatabaseException(minorVersion, major, minor);
                }
                break;
            default:
                throw new UnsupportedDatabaseException(minorVersion, major, minor);
        }
        return database;
    }


    private static Throwable convertToCustomException(Throwable throwable
            , @Nullable Function<RuntimeException, RuntimeException> springFunction
            , @Nullable Function<RuntimeException, RuntimeException> customFunction) {
        if (throwable instanceof Error) {
            return throwable;
        }
        RuntimeException convertedException = convertToSessionException((Exception) throwable);
        if (springFunction != null) {
            convertedException = springFunction.apply(convertedException);
        }
        if (customFunction != null) {
            convertedException = customFunction.apply(convertedException);
        }
        return convertedException;
    }

    protected static SessionException convertToSessionException(Exception ex) {

        // TODO zoro finish this
        return new SessionUsageException(ex, "");
    }

    protected static class DatabaseMeta {


        private final String productName;

        private final int majorVersion;

        private final int minorVersion;

        private final String catalog;

        private final String schema;

        private final boolean supportSavePoint;

        protected DatabaseMeta(String productName, int majorVersion, int minorVersion
                , @Nullable String catalog, @Nullable String schema, boolean supportSavePoint) {
            this.productName = productName;
            this.majorVersion = majorVersion;
            this.minorVersion = minorVersion;
            this.catalog = catalog == null ? "" : catalog;
            this.schema = schema == null ? "" : schema;
            this.supportSavePoint = supportSavePoint;
        }

        public String getProductName() {
            return this.productName;
        }

        public int getMajorVersion() {
            return this.majorVersion;
        }

        public int getMinorVersion() {
            return this.minorVersion;
        }

        public String getCatalog() {
            return this.catalog;
        }

        public String getSchema() {
            return this.schema;
        }

        public boolean isSupportSavePoint() {
            return this.supportSavePoint;
        }
    }

}
