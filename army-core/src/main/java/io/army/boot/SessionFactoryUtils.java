package io.army.boot;

import io.army.ArmyAccessException;
import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.asm.TableMetaLoader;
import io.army.criteria.MetaException;
import io.army.dialect.Dialect;
import io.army.dialect.DialectNotMatchException;
import io.army.dialect.SQLDialect;
import io.army.dialect.UnSupportedDialectException;
import io.army.dialect.mysql.MySQLDialectFactory;
import io.army.env.Environment;
import io.army.generator.GeneratorFactory;
import io.army.generator.MultiGenerator;
import io.army.generator.PreMultiGenerator;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Pair;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * @see SessionFactory
 */
abstract class SessionFactoryUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SessionFactoryUtils.class);

    static final class GeneratorWrapper {

        private final Map<FieldMeta<?, ?>, MultiGenerator> generatorChain;

        private final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain;

        public GeneratorWrapper(Map<FieldMeta<?, ?>, MultiGenerator> generatorChain
                , Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain) {
            this.generatorChain = Collections.unmodifiableMap(generatorChain);
            this.tableGeneratorChain = Collections.unmodifiableMap(tableGeneratorChain);
        }

        /**
         * @return a modifiable map
         */
        public Map<FieldMeta<?, ?>, MultiGenerator> getGeneratorChain() {
            return generatorChain;
        }

        /**
         * @return a modifiable map
         */
        public Map<TableMeta<?>, List<FieldMeta<?, ?>>> getTableGeneratorChain() {
            return tableGeneratorChain;
        }
    }


    static Pair<Dialect, SQLDialect> createDialect(final @Nullable SQLDialect sqlDialect, DataSource dataSource,
                                                   SessionFactory sessionFactory) {
        final SQLDialect databaseSqlDialect = getSQLDialect(dataSource);

        SQLDialect actualSqlDialect = decideSQLDialect(sqlDialect, databaseSqlDialect);

        Dialect dialect;
        switch (actualSqlDialect) {
            case MySQL:
            case MySQL57:
            case MySQL80:
                dialect = MySQLDialectFactory.createMySQLDialect(actualSqlDialect, sessionFactory);
                break;
            case Db2:
            case Oracle:
            case Postgre:
            case OceanBase:
            case SQL_Server:
            default:
                throw new RuntimeException(String.format("unknown SQLDialect[%s]", actualSqlDialect));
        }
        return new Pair<>(dialect, databaseSqlDialect);
    }

    static Map<Class<?>, TableMeta<?>> scanPackagesForMeta(SchemaMeta schemaMeta, List<String> packagesToScan) {
        return TableMetaLoader.build()
                .scanTableMeta(schemaMeta, packagesToScan);
    }

    static GeneratorWrapper createGeneratorWrapper(Collection<TableMeta<?>> tableMetas, Environment environment) {
        final Map<FieldMeta<?, ?>, MultiGenerator> generatorMap = new HashMap<>();
        final Map<TableMeta<?>, List<FieldMeta<?, ?>>> tableGeneratorChain = new HashMap<>();

        for (TableMeta<?> tableMeta : tableMetas) {
            Map<String, GeneratorMeta> propGeneratorMap = new HashMap<>();

            for (FieldMeta<?, ?> fieldMeta : tableMeta.fieldCollection()) {
                GeneratorMeta generatorMeta = fieldMeta.generator();
                if (generatorMeta == null) {
                    continue;
                }
                if (StringUtils.hasText(generatorMeta.dependPropName())
                        && !tableMeta.isMappingProp(generatorMeta.dependPropName())) {
                    throw createDependException(fieldMeta);
                }
                MultiGenerator generator = GeneratorFactory.getGenerator(fieldMeta, environment);
                // create generator
                generatorMap.put(fieldMeta,generator );

                if(generator instanceof PreMultiGenerator){
                    propGeneratorMap.put(fieldMeta.propertyName(), generatorMeta);
                }
            }

            if (!propGeneratorMap.isEmpty()) {
                List<FieldMeta<?, ?>> chain = createTableGeneratorChain(tableMeta, propGeneratorMap);
                tableGeneratorChain.put(tableMeta, chain);
            }

        }
        return new GeneratorWrapper(generatorMap, tableGeneratorChain);
    }


    /*################################## blow private method ##################################*/

    private static SQLDialect oracleDialect(int major, int minor) {
        throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                , "%s is unsupported by army.", "Oracle");
    }

    private static SQLDialect mysqlDialect(int major, int minor) {
        SQLDialect sqlDialect;
        switch (major) {
            case 5:
                if (minor < 7) {
                    throw createUnSupportedDialectException(major, minor);
                }
                sqlDialect = SQLDialect.MySQL57;
                break;
            case 8:
                switch (minor) {
                    case 0:
                        sqlDialect = SQLDialect.MySQL80;
                        break;
                    default:
                        throw createUnSupportedDialectException(major, minor);
                }
                break;
            default:
                throw createUnSupportedDialectException(major, minor);
        }
        return sqlDialect;
    }

    private static SQLDialect decideSQLDialect(@Nullable SQLDialect dialect, SQLDialect databaseSqlDialect) {
        SQLDialect actual = dialect;
        if (actual == null) {
            LOG.debug("extract sql dialect from database");
            actual = databaseSqlDialect;
        } else if (!SQLDialect.sameFamily(dialect, databaseSqlDialect)
                || dialect.ordinal() > databaseSqlDialect.ordinal()) {
            throw new DialectNotMatchException(ErrorCode.META_ERROR, "SQLDialect[%s] and database not match.", actual);
        }
        return actual;
    }

    private static SQLDialect getSQLDialect(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();

            String productName = metaData.getDatabaseProductName();
            int major = metaData.getDatabaseMajorVersion();
            int minor = metaData.getDatabaseMinorVersion();

            SQLDialect sqlDialect;
            switch (productName) {
                case "MySQL":
                    sqlDialect = mysqlDialect(major, minor);
                    break;
                case "Oracle":
                    sqlDialect = oracleDialect(major, minor);
                    break;
                default:
                    throw new UnSupportedDialectException(ErrorCode.UNSUPPORT_DIALECT
                            , "%s is unsupported by army.", productName);
            }
            return sqlDialect;
        } catch (SQLException e) {
            throw new ArmyAccessException(ErrorCode.ACCESS_ERROR, e, e.getMessage());
        }
    }


    private static UnSupportedDialectException createUnSupportedDialectException(int major, int minor) {
        return new UnSupportedDialectException(ErrorCode.UNSUPPORTED_DIALECT
                , "MySQL %s.%s.x is supported by army", major, minor);
    }


    /**
     * @return a unmodifiable list
     */
    private static List<FieldMeta<?, ?>> createTableGeneratorChain(TableMeta<?> tableMeta
            , Map<String, GeneratorMeta> propGeneratorMap) {

       final List<Set<String>> dependLevelList = new ArrayList<>();
       final Set<String> ancestorLevelSet = new HashSet<>();
        dependLevelList.add(ancestorLevelSet);

        //1. add ancestorLevelSet
        for (Map.Entry<String, GeneratorMeta> e : propGeneratorMap.entrySet()) {
            if (!StringUtils.hasText(e.getValue().dependPropName())) {
                ancestorLevelSet.add(e.getKey());
            }
        }
        //2. add child level after ancestor level
        appendChildLevel(dependLevelList, propGeneratorMap);

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
            // continue append next level
            appendChildLevel(dependLevelList, propGeneratorMap);
        }
    }

    private static MetaException createDependException(FieldMeta<?, ?> fieldMeta) {
        return new MetaException(ErrorCode.META_ERROR, "Entity[%s] propName[%s] generator depend error"
                , fieldMeta.table().javaType().getName()
                , fieldMeta.propertyName());
    }

}