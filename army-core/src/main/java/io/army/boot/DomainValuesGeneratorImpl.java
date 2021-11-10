package io.army.boot;

import io.army.ErrorCode;
import io.army.beans.DomainWrapper;
import io.army.beans.ObjectAccessorFactory;
import io.army.beans.ObjectWrapper;
import io.army.criteria.CriteriaException;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.session.GenericSessionFactory;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

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

        entityWrapper.set(fieldMeta.fieldName(), value);
    }

    /**
     * create required value for entity,eg : prop annotated by @Generator ,createTime,updateTime,version
     */
    private void createValuesManagedByArmy(
            TableMeta<?> tableMeta, ObjectWrapper entityWrapper) {
        TableMeta<?> parentMeta;
        if (tableMeta instanceof ChildTableMeta) {
            parentMeta = ((ChildTableMeta<?>) tableMeta).parentMeta();
        } else {
            parentMeta = tableMeta;
        }
        ZonedDateTime now = ZonedDateTime.now(this.sessionFactory.zoneOffset());
        createCreateOrUpdateTime(parentMeta.getField(_MetaBridge.CREATE_TIME), now, entityWrapper);

        if (!tableMeta.immutable()) {
            createCreateOrUpdateTime(parentMeta.getField(_MetaBridge.UPDATE_TIME), now, entityWrapper);
            if (parentMeta.mappingField(_MetaBridge.VERSION)) {
                // create version value
                entityWrapper.set(_MetaBridge.VERSION, 0);
            }
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
        CodeEnum codeEnum = CodeEnum.resolve(discriminator.javaType(), tableMeta.discriminatorValue());
        entityWrapper.set(discriminator.fieldName(), codeEnum);
    }

    private void createCreateOrUpdateTime(FieldMeta<?, ?> fieldMeta, ZonedDateTime now, ObjectWrapper entityWrapper) {
        if (fieldMeta.javaType() == LocalDateTime.class) {
            entityWrapper.set(fieldMeta.fieldName(), now.toLocalDateTime());
        } else if (fieldMeta.javaType() == ZonedDateTime.class) {
            assertDialectSupportedZone();
            entityWrapper.set(fieldMeta.fieldName(), now);
        } else {
            throw new MetaException("createTime or updateTime only support LocalDateTime or ZonedDateTime");
        }
    }

    private void assertDialectSupportedZone() {
        if (!this.sessionFactory.supportZone()) {
            throw new MetaException("%s not support zone"
                    , this.sessionFactory);
        }
    }


}
