package io.army.boot;

import io.army.ErrorCode;
import io.army.GenericRmSessionFactory;
import io.army.beans.BeanWrapper;
import io.army.beans.DomainWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.criteria.CriteriaException;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.meta.ChildTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

final class DomainValuesGeneratorImpl implements DomainValuesGenerator {

    private final GenericRmSessionFactory sessionFactory;

    DomainValuesGeneratorImpl(GenericRmSessionFactory sessionFactory) {
        Assert.notNull(sessionFactory, "sessionFactory required");
        this.sessionFactory = sessionFactory;
    }

    @Override
    public final DomainWrapper createValues(TableMeta<?> tableMeta, IDomain domain, boolean migrationData)
            throws FieldValuesCreateException {

        Assert.isTrue(tableMeta.javaType() == domain.getClass(), "tableMeta then entity not match");

        final DomainWrapper domainWrapper = ObjectAccessorFactory.forDomainPropertyAccess(
                domain, this.sessionFactory.tableMeta(domain.getClass()));

        createValues(domainWrapper, migrationData);
        return domainWrapper;
    }

    @Override
    public final void createValues(DomainWrapper domainWrapper, boolean migrationData) {
        final TableMeta<?> tableMeta = domainWrapper.tableMeta();
        if (migrationData) {
            // discriminator
            createDiscriminatorValue(tableMeta, domainWrapper);
        } else {
            createValuesManagedByArmy(tableMeta, domainWrapper);
            if (tableMeta instanceof ChildTableMeta) {
                ChildTableMeta<?> childMeta = (ChildTableMeta<?>) tableMeta;
                createValuesWithGenerator(childMeta.parentMeta(), domainWrapper);
            }
            createValuesWithGenerator(tableMeta, domainWrapper);
        }
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
        TableMeta<?> parentMeta;
        if (tableMeta instanceof ChildTableMeta) {
            parentMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
        } else {
            parentMeta = tableMeta;
        }
        ZonedDateTime now = ZonedDateTime.now(this.sessionFactory.zoneId());
        createCreateOrUpdateTime(parentMeta.getField(TableMeta.CREATE_TIME), now, entityWrapper);

        if (!tableMeta.immutable()) {
            createCreateOrUpdateTime(parentMeta.getField(TableMeta.UPDATE_TIME), now, entityWrapper);
            if (parentMeta.mappingProp(TableMeta.VERSION)) {
                // create version value
                entityWrapper.setPropertyValue(TableMeta.VERSION, 0);
            }
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
        CodeEnum codeEnum = CodeEnum.resolve(discriminator.javaType(), tableMeta.discriminatorValue());
        entityWrapper.setPropertyValue(discriminator.propertyName(), codeEnum);
    }

    private void createCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, ZonedDateTime now, BeanWrapper entityWrapper) {
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
        if (!this.sessionFactory.supportZone()) {
            throw new MetaException("dialect[%s] unsupported zoneId"
                    , this.sessionFactory.actualDatabase());
        }
    }


}
