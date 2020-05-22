package io.army.boot;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.DomainWrapper;
import io.army.beans.ObjectWrapper;
import io.army.beans.PropertyAccessorFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
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

final class FieldValuesGeneratorImpl implements FieldValuesGenerator {

    private final GenericSessionFactory sessionFactory;

    FieldValuesGeneratorImpl(GenericSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain) throws FieldValuesCreateException {

        Assert.isTrue(tableMeta.javaType() == domain.getClass(), "tableMeta then entity not match");

        final DomainWrapper entityWrapper = PropertyAccessorFactory.forDomainPropertyAccess(domain);

        createValuesManagedByArmy(tableMeta, entityWrapper);

        createValuesWithGenerator(tableMeta, entityWrapper);
        return entityWrapper;
    }

    /*################################## blow private method ##################################*/

    private void createValuesWithGenerator(TableMeta<?> tableMeta, ObjectWrapper domainWrapper) {
        List<FieldMeta<?, ?>> chain = sessionFactory.tableGeneratorChain().get(tableMeta);
        if (CollectionUtils.isEmpty(chain)) {
            return;
        }
        int index = 0;
        for (FieldMeta<?, ?> fieldMeta : chain) {
            if (index == 0) {
                assertFirstDependency(fieldMeta, domainWrapper);
            }
            FieldGenerator generator = this.sessionFactory.fieldGenerator(fieldMeta);
            if (!(generator instanceof PreFieldGenerator)) {
                continue;
            }
            doCreateValueWithGenerator(fieldMeta, (PreFieldGenerator) generator, domainWrapper);
            index++;
        }
    }

    private void assertFirstDependency(FieldMeta<?, ?> fieldMeta, ObjectWrapper domainWrapper) {
        GeneratorMeta generatorMeta = fieldMeta.generator();

        Assert.state(generatorMeta != null
                , () -> String.format("GeneratorMeta of FieldMeta[%s] error.", fieldMeta));
        String dependencyName = generatorMeta.dependPropName();
        if (StringUtils.hasText(dependencyName) && (!domainWrapper.isReadableProperty(dependencyName)
                || domainWrapper.getPropertyValue(dependencyName) == null)) {

            throw new CriteriaException(ErrorCode.CRITERIA_ERROR
                    , "Domain[%s].%s is null,FieldGenerator can't work."
                    , fieldMeta.tableMeta().javaType().getName()
                    , generatorMeta.dependPropName());
        }
    }

    private void doCreateValueWithGenerator(FieldMeta<?, ?> fieldMeta, PreFieldGenerator generator
            , ObjectWrapper entityWrapper) {
        // invoke generator
        Object value = generator.next(fieldMeta, entityWrapper);

        Assert.state(fieldMeta.javaType().isInstance(value)
                , () -> String.format("TableMeta[%s].FieldMeta[%s] FieldGenerator[%s] return error value."
                        , fieldMeta.tableMeta()
                        , fieldMeta
                        , generator));

        entityWrapper.setPropertyValue(fieldMeta.propertyName(), value);
    }

    /**
     * create required value for entity,eg : prop annotated by @Generator ,createTime,updateTime,version
     */
    private void createValuesManagedByArmy(
            TableMeta<?> tableMeta, ObjectWrapper entityWrapper) {

        createCreateOrUpdateTime(tableMeta.getField(TableMeta.CREATE_TIME), entityWrapper);

        if (!tableMeta.immutable()) {
            createCreateOrUpdateTime(tableMeta.getField(TableMeta.UPDATE_TIME), entityWrapper);
            // create version value
            entityWrapper.setPropertyValue(TableMeta.VERSION, 0);
        }
        // discriminator
        createDiscriminatorValue(tableMeta, entityWrapper);
    }

    private <T extends IDomain, E extends Enum<E> & CodeEnum> void createDiscriminatorValue(
            TableMeta<T> tableMeta, ObjectWrapper entityWrapper) {
        FieldMeta<?, E> discriminator = tableMeta.discriminator();

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

    private void createCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, ObjectWrapper entityWrapper) {
        ZonedDateTime now = ZonedDateTime.now(this.sessionFactory.zoneId());
        if (fieldMeta.javaType() == LocalDateTime.class) {
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), now.toLocalDateTime());
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            assertDialectSupportedZone();
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), now);
        } else {
            throw new MetaException(ErrorCode.META_ERROR
                    , "createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void assertDialectSupportedZone() {
        if (!this.sessionFactory.supportZoneId()) {
            throw new MetaException(ErrorCode.META_ERROR, "dialect[%s] unsupported zoneId"
                    , this.sessionFactory.actualSQLDialect());
        }
    }


}
