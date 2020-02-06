package io.army.boot;

import io.army.ErrorCode;
import io.army.Session;
import io.army.beans.BeanWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.generator.MultiGenerator;
import io.army.generator.PreMultiGenerator;
import io.army.meta.FieldMeta;
import io.army.meta.MappingMode;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

final class FieldValuesGeneratorImpl implements FieldValuesGenerator {

    private final Session session;

    FieldValuesGeneratorImpl(Session session) {
        Assert.notNull(session, "sessionFactory required");
        this.session = session;
    }

    @Override
    public BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity) throws FieldValuesCreateException {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entity, "entity required");
        Assert.isTrue(tableMeta.javaType() == entity.getClass(), "tableMata then entity not match");

        final BeanWrapper entityWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        createValuesManagedByArmy(tableMeta, entityWrapper);

        createValuesWithGenerator(tableMeta, entityWrapper);
        return entityWrapper;
    }

    /*################################## blow private method ##################################*/

    private void createValuesWithGenerator(TableMeta<?> tableMeta, BeanWrapper entityWrapper) {
        List<FieldMeta<?, ?>> chain = session.sessionFactory().tableGeneratorChain().get(tableMeta);
        if (CollectionUtils.isEmpty(chain)) {
            return;
        }
        Map<FieldMeta<?, ?>, MultiGenerator> generatorMap = session.sessionFactory().fieldGeneratorMap();
        for (FieldMeta<?, ?> fieldMeta : chain) {
            MultiGenerator generator = generatorMap.get(fieldMeta);
            Assert.isInstanceOf(PreMultiGenerator.class, generator);

            doCreateValueWithGenerator(fieldMeta, (PreMultiGenerator) generator, entityWrapper);
        }
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
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] discriminator couldn'table convert to %s type."
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
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), ZonedDateTime.now(session.sessionFactory().zoneId()));
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void assertDialectSupportedZone() {
        if (!session.sessionFactory().dialect().supportZoneId()) {
            throw new MetaException(ErrorCode.META_ERROR, "dialect[%s] unsupported zoneId"
                    , session.sessionFactory().dialect().sqlDialect());
        }
    }


}
