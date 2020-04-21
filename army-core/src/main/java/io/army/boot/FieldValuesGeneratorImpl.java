package io.army.boot;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.beans.BeanWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.generator.MultiGenerator;
import io.army.generator.PreMultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

final class FieldValuesGeneratorImpl implements FieldValuesGenerator {

    private static final ConcurrentMap<SessionFactory, FieldValuesGeneratorImpl> CACHE = new ConcurrentHashMap<>();

    static FieldValuesGeneratorImpl build(SessionFactory sessionFactory) {
        return CACHE.computeIfAbsent(sessionFactory, FieldValuesGeneratorImpl::new);
    }

    private final SessionFactory sessionFactory;

    private FieldValuesGeneratorImpl(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity) throws FieldValuesCreateException {
        return createValues(tableMeta, entity, false);
    }

    @Override
    public final BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity, boolean noDependValueAbort)
            throws FieldValuesCreateException {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entity, "entity required");
        Assert.isTrue(tableMeta.javaType() == entity.getClass(), "tableMeta then entity not match");

        final BeanWrapper entityWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        createValuesManagedByArmy(tableMeta, entityWrapper);

        createValuesWithGenerator(tableMeta, entityWrapper, noDependValueAbort);
        return entityWrapper;
    }

    /*################################## blow private method ##################################*/

    private void createValuesWithGenerator(TableMeta<?> tableMeta, BeanWrapper entityWrapper
            , boolean noDependValueAbort) {
        List<FieldMeta<?, ?>> chain = sessionFactory.tableGeneratorChain().get(tableMeta);
        if (CollectionUtils.isEmpty(chain)) {
            return;
        }
        Map<FieldMeta<?, ?>, MultiGenerator> generatorMap = sessionFactory.fieldGeneratorMap();
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : chain) {
            if (index == 0 && ignoreAndAbort(fieldMeta, entityWrapper, noDependValueAbort)) {
                return;
            }
            MultiGenerator generator = generatorMap.get(fieldMeta);
            Assert.isInstanceOf(PreMultiGenerator.class, generator);

            doCreateValueWithGenerator(fieldMeta, (PreMultiGenerator) generator, entityWrapper);
            index++;
        }
    }

    private boolean ignoreAndAbort(FieldMeta<?, ?> fieldMeta, BeanWrapper entityWrapper, boolean noDependValueAbort) {
        GeneratorMeta generatorMeta = fieldMeta.generator();

        boolean ignoreAndAbort = false;
        Assert.state(generatorMeta != null
                , () -> String.format("GeneratorMeta of FieldMeta[%s] error.", fieldMeta));
        if (StringUtils.hasText(generatorMeta.dependPropName())
                && entityWrapper.getPropertyValue(fieldMeta.propertyName()) == null) {

            if (noDependValueAbort) {
                ignoreAndAbort = true;
            } else {
                throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                        , "Entity[%s].%s is null,MultiGenerator can't work."
                        , fieldMeta.tableMeta().javaType().getName()
                        , generatorMeta.dependPropName());
            }
        }
        return ignoreAndAbort;
    }

    private void doCreateValueWithGenerator(FieldMeta<?, ?> fieldMeta, PreMultiGenerator generator
            , BeanWrapper entityWrapper) {
        Object value = generator.next(fieldMeta, entityWrapper);

        Assert.state(fieldMeta.javaType().isInstance(value)
                , () -> String.format("entity[%s].field[%s] PreMultiGenerator[%s] return error value."
                        , fieldMeta.tableMeta().javaType().getName()
                        , fieldMeta.propertyName()
                        , generator.getClass().getName()));

        entityWrapper.setPropertyValue(fieldMeta.propertyName(), value);
    }

    /**
     * create required value for entity,eg : prop annotated by @Generator ,createTime,updateTime,version
     */
    private void createValuesManagedByArmy(
            TableMeta<?> tableMeta, BeanWrapper entityWrapper) {

        createCreateOrUpdateTime(tableMeta.getField(TableMeta.CREATE_TIME), entityWrapper);
        createCreateOrUpdateTime(tableMeta.getField(TableMeta.UPDATE_TIME), entityWrapper);
        // create version value
        entityWrapper.setPropertyValue(TableMeta.VERSION, 0);
        // discriminator
        createDiscriminatorValue(tableMeta, entityWrapper);
    }

    private <T extends IDomain, E extends Enum<E> & CodeEnum> void createDiscriminatorValue(
            TableMeta<T> tableMeta, BeanWrapper entityWrapper) {
        FieldMeta<T, E> discriminator = tableMeta.discriminator();

        if (discriminator == null) {
            return;
        }
        if (tableMeta.mappingMode() != MappingMode.PARENT) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator meta error"
                    , tableMeta.javaType().getName());
        }

        Map<Integer, E> codeMap = CodeEnum.getCodeMap(discriminator.javaType());
        E e = codeMap.get(tableMeta.discriminatorValue());
        if (e == null) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator couldn'field convert to %s type."
                    , tableMeta.javaType().getName()
                    , discriminator.javaType().getName()
            );
        }
        entityWrapper.setPropertyValue(discriminator.propertyName(), e);
    }

    private void createCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, BeanWrapper entityWrapper) {
        if (fieldMeta.javaType() == LocalDateTime.class) {
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), LocalDateTime.now());
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            assertDialectSupportedZone();
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), ZonedDateTime.now(sessionFactory.zoneId()));
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void assertDialectSupportedZone() {
        if (!sessionFactory.dialect().supportZoneId()) {
            throw new MetaException(ErrorCode.META_ERROR, "dialect[%s] unsupported zoneId"
                    , sessionFactory.dialect().sqlDialect());
        }
    }


}
