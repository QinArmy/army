package io.army.generator;

import io.army.ErrorCode;
import io.army.SessionFactory;
import io.army.annotation.Table;
import io.army.beans.BeanWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.util.Assert;
import io.army.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

final class FieldValuesGeneratorImpl implements FieldValuesGenerator {

    private final SessionFactory sessionFactory;

    FieldValuesGeneratorImpl(SessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public BeanWrapper createValues(TableMeta<?> tableMeta, IDomain entity) throws FieldValuesCreateException {
        Assert.notNull(tableMeta, "tableMeta required");
        Assert.notNull(entity, "entity required");
        Assert.isTrue(tableMeta.javaType() == entity.getClass(), "tableMata and entity not match");

        final BeanWrapper entityWrapper = PropertyAccessorFactory.forBeanPropertyAccess(entity);

        createValuesManagedByArmy(tableMeta, entityWrapper);

        createValuesWithGenerator(tableMeta, entityWrapper);
        return entityWrapper;
    }

    /*################################## blow private method ##################################*/

    private void createValuesWithGenerator(TableMeta<?> tableMeta, BeanWrapper entityWrapper) {
        List<FieldMeta<?, ?>> chain = sessionFactory.tableGeneratorChain().get(tableMeta);
        if (CollectionUtils.isEmpty(chain)) {
            return;
        }
        Map<FieldMeta<?, ?>, MultiGenerator> generatorMap = sessionFactory.fieldGeneratorMap();
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
                        , fieldMeta.table().javaType().getName()
                        , fieldMeta.propertyName()
                        , generator.getClass().getName()));

        entityWrapper.setPropertyValue(fieldMeta.propertyName(), value);
    }

    /**
     * create required value for entity,eg : prop annotated by @Generator ,createTime,updateTime,version
     */
    private void createValuesManagedByArmy(TableMeta<?> tableMeta, BeanWrapper entityWrapper) {
        createCreateOrUpdateTime(tableMeta.getField(TableMeta.CREATE_TIME), entityWrapper);
        createCreateOrUpdateTime(tableMeta.getField(TableMeta.UPDATE_TIME), entityWrapper);
        // create version value
        entityWrapper.setPropertyValue(TableMeta.VERSION, 0);
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
