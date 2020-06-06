package io.army.boot;

import io.army.ErrorCode;
import io.army.GenericSessionFactory;
import io.army.beans.AccessorFactory;
import io.army.beans.BeanWrapper;
import io.army.beans.DomainWrapper;
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
import io.army.util.ReflectionUtils;
import io.army.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

final class DomainValuesGeneratorImpl implements DomainValuesGenerator {

    private final GenericSessionFactory sessionFactory;

    DomainValuesGeneratorImpl(GenericSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain, boolean migrationData)
            throws FieldValuesCreateException {

        Assert.isTrue(tableMeta.javaType() == domain.getClass(), "tableMeta then entity not match");

        final DomainWrapper entityWrapper = AccessorFactory.forDomainPropertyAccess(
                domain, this.sessionFactory.tableMeta(domain.getClass()));

        if (migrationData) {
            // discriminator
            createDiscriminatorValue(tableMeta, entityWrapper);
        } else {
            createValuesManagedByArmy(tableMeta, entityWrapper);

            createValuesWithGenerator(tableMeta, entityWrapper);
        }
        return entityWrapper;
    }


    /*################################## blow private method ##################################*/

    private void createValuesWithGenerator(TableMeta<?> tableMeta, BeanWrapper domainWrapper) {
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

    private void assertFirstDependency(FieldMeta<?, ?> fieldMeta, BeanWrapper domainWrapper) {
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
            , BeanWrapper entityWrapper) {
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
            TableMeta<?> tableMeta, BeanWrapper entityWrapper) {

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
            TableMeta<T> tableMeta, BeanWrapper entityWrapper) {
        FieldMeta<?, E> discriminator = tableMeta.discriminator();

        if (discriminator == null) {
            return;
        }
        if (tableMeta.mappingMode() != MappingMode.PARENT) {
            throw new MetaException("entity[%s] discriminator meta error"
                    , tableMeta.javaType().getName());
        }
        Method method = ReflectionUtils.findMethod(discriminator.javaType(), "resolve", int.class);
        if (method == null
                || !Modifier.isStatic(method.getModifiers())
                || !Modifier.isPublic(method.getModifiers())
                || method.getReturnType() != discriminator.javaType()) {
            throw new MetaException("CodeEnum[%s] discriminator no resolve method."
                    , discriminator.javaType());
        }
        try {
            Object discriminatorValue = method.invoke(null, tableMeta.discriminatorValue());
            if (discriminatorValue == null) {
                throw new MetaException("TableMeta[%s] discriminator[%s]'s CodeEnum is null."
                        , tableMeta, tableMeta.discriminatorValue());
            }
            entityWrapper.setPropertyValue(discriminator.propertyName(), discriminatorValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MetaException(e, "TableMeta[%s] discriminator[%s] resolve method error."
                    , tableMeta, discriminator.javaType());
        }
    }

    private void createCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, BeanWrapper entityWrapper) {
        ZonedDateTime now = ZonedDateTime.now(this.sessionFactory.zoneId());
        if (fieldMeta.javaType() == LocalDateTime.class) {
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), now.toLocalDateTime());
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            assertDialectSupportedZone();
            entityWrapper.setPropertyValue(fieldMeta.propertyName(), now);
        } else {
            throw new MetaException("createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void assertDialectSupportedZone() {
        if (!this.sessionFactory.supportZoneId()) {
            throw new MetaException("dialect[%s] unsupported zoneId"
                    , this.sessionFactory.actualSQLDialect());
        }
    }


}
