package io.army.boot;

import io.army.*;
import io.army.asm.TableMetaLoader;
import io.army.codec.FieldCodec;
import io.army.criteria.MetaException;
import io.army.criteria.impl.SchemaMetaFactory;
import io.army.criteria.impl.TableMetaFactory;
import io.army.env.Environment;
import io.army.generator.FieldGenerator;
import io.army.generator.GeneratorFactory;
import io.army.generator.PreFieldGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.SchemaMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneId;
import java.util.*;

/**
 * @see GenericSessionFactory
 */
abstract class SessionFactoryUtils {

    static final Logger LOG = LoggerFactory.getLogger(SessionFactoryUtils.class);

    protected SessionFactoryUtils() {
        throw new UnsupportedOperationException();
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

    static boolean sessionCache(Environment env, String factoryName) {
        return env.getProperty(String.format(ArmyConfigConstant.SESSION_CACHE, factoryName)
                , Boolean.class, Boolean.TRUE);
    }

    static Map<Class<?>, TableMeta<?>> scanPackagesForMeta(SchemaMeta schemaMeta, String factoryName, Environment env) {
        List<String> packagesToScan = env.getRequiredPropertyList(
                String.format(ArmyConfigConstant.PACKAGE_TO_SCAN, factoryName), String[].class);
        return TableMetaLoader.build()
                .scanTableMeta(schemaMeta, packagesToScan);
    }

    static ShardingMode shardingMode(String factoryName, Environment env) {
        return env.getProperty(String.format(ArmyConfigConstant.SHARDING_MODE, factoryName)
                , ShardingMode.class, ShardingMode.NO_SHARDING);
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
                if (sessionFactory.shardingMode() != ShardingMode.NO_SHARDING
                        && PreFieldGenerator.class.isAssignableFrom(generatorMeta.type())) {
                    throw new SessionFactoryException(ErrorCode.SESSION_FACTORY_CREATE_ERROR
                            , "%s not supported by %s.", PreFieldGenerator.class.getName()
                            , sessionFactory.shardingMode());
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

    static SchemaMeta obtainSchemaMeta(String factoryName, Environment env) {
        String catalog = env.getProperty(String.format("army.sessionFactory.%s.catalog", factoryName), "");
        String schema = env.getProperty(String.format("army.sessionFactory.%s.schema", factoryName), "");
        return SchemaMetaFactory.getSchema(catalog, schema);
    }

    static ZoneId createZoneId(Environment env, String factoryName) {
        String zoneIdText = env.getProperty(String.format(ArmyConfigConstant.ZONE_ID, factoryName));
        ZoneId zoneId;
        if (StringUtils.hasText(zoneIdText)) {
            zoneId = ZoneId.of(zoneIdText);
        } else {
            zoneId = ZoneId.systemDefault();
        }
        return zoneId;
    }

    static boolean readOnly(String factoryName, Environment env) {
        return env.getProperty(String.format(ArmyConfigConstant.READ_ONLY, factoryName), Boolean.class, Boolean.FALSE);
    }

    static Map<FieldMeta<?, ?>, FieldCodec> createTableFieldCodecMap(
            Collection<FieldCodec> fieldCodecs) {

        Map<FieldMeta<?, ?>, FieldCodec> fieldCodecMap = new HashMap<>();

        for (FieldCodec codec : fieldCodecs) {

            for (FieldMeta<?, ?> fieldMeta : codec.fieldMetaSet()) {
                if (!fieldMeta.codec()) {
                    throw new SessionFactoryException(ErrorCode.NON_CODEC_FIELD
                            , "FieldMeta[%s] don't support FieldCodec.", fieldMeta);
                }
                if (fieldCodecMap.putIfAbsent(fieldMeta, codec) != null) {
                    throw new SessionFactoryException(ErrorCode.FIELD_CODEC_DUPLICATION
                            , "FieldMeta[%s]'s FieldCodec[%s] duplication.", fieldMeta, codec);
                }
            }
        }
        // check all codec field have FieldCodec
        Set<FieldMeta<?, ?>> codecFieldSet = new HashSet<>(fieldCodecMap.keySet());
        codecFieldSet.removeAll(TableMetaFactory.codecFieldMetaSet());
        if (!codecFieldSet.isEmpty()) {
            throw new SessionFactoryException(ErrorCode.NO_FIELD_CODEC
                    , "FieldMeta set [%s] not found FieldCodec.", codecFieldSet);
        }
        return Collections.unmodifiableMap(fieldCodecMap);
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

}
