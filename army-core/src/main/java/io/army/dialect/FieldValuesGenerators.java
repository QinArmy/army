package io.army.dialect;

import io.army.bean.ReadWrapper;
import io.army.criteria.CriteriaException;
import io.army.generator.FieldGenerator;
import io.army.generator.GeneratorException;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

abstract class FieldValuesGenerators implements FieldValueGenerator {


    static FieldValuesGenerators create(Supplier<ZoneId> zoneIdSupplier
            , Map<FieldMeta<?>, FieldGenerator> generatorMap) {
        return new DefaultFieldValuesGenerator(zoneIdSupplier, generatorMap);
    }

    static FieldValuesGenerators mock(Supplier<ZoneId> zoneIdSupplier) {
        return new MockFieldValuesGenerator(zoneIdSupplier);
    }


    private final Supplier<ZoneId> zoneIdSupplier;

    private FieldValuesGenerators(Supplier<ZoneId> zoneIdSupplier) {
        this.zoneIdSupplier = zoneIdSupplier;
    }

    @Override
    public final void generate(final TableMeta<?> domainTable, final boolean manegeVisible
            , final RowWrapper wrapper) {

        if (!(domainTable instanceof SimpleTableMeta)) {
            final FieldMeta<?> discriminator;
            discriminator = domainTable.discriminator();
            assert discriminator != null;

            final CodeEnum codeEnum;
            codeEnum = CodeEnum.resolve(discriminator.javaType(), domainTable.discriminatorValue());
            if (codeEnum == null) {
                throw _Exceptions.discriminatorNoMapping(domainTable);
            }
            wrapper.set(discriminator, codeEnum);
        }

        if (domainTable instanceof ChildTableMeta) {
            final ParentTableMeta<?> parentTable = ((ChildTableMeta<?>) domainTable).parentMeta();
            reservedFields(parentTable, manegeVisible, wrapper);
            generatorChan(parentTable.fieldChain(), wrapper);
        } else {
            reservedFields((SingleTableMeta<?>) domainTable, manegeVisible, wrapper);
        }

        generatorChan(domainTable.fieldChain(), wrapper);
    }

    @Override
    public final void validate(final TableMeta<?> domainTable, final RowWrapper wrapper) {

        if (!(domainTable instanceof SimpleTableMeta)) {
            final FieldMeta<?> discriminator = domainTable.discriminator();
            assert discriminator != null;
            final CodeEnum codeEnum;
            codeEnum = CodeEnum.resolve(discriminator.javaType(), domainTable.discriminatorValue());
            if (codeEnum == null) {
                throw _Exceptions.discriminatorNoMapping(domainTable);
            }
            if (wrapper.readonlyWrapper().get(discriminator.fieldName()) != codeEnum) {
                Class<?> enumClass = codeEnum.getClass();
                if (enumClass.isAnonymousClass()) {
                    enumClass = enumClass.getSuperclass();
                }
                String m = String.format("%s discriminator value isn't %s.%s"
                        , domainTable, enumClass.getName(), codeEnum.name());
                throw new CriteriaException(m);
            }

        }

        final TableMeta<?> nonChild;
        if (domainTable instanceof ChildTableMeta) {
            nonChild = ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            nonChild = domainTable;
        }

        if (wrapper.isNullValueParam(nonChild.id())) {
            throw nullValueErrorForMigration(nonChild.id());
        }
        if (wrapper.isNullValueParam(nonChild.getField(_MetaBridge.CREATE_TIME))) {
            throw nullValueErrorForMigration(nonChild.getField(_MetaBridge.CREATE_TIME));
        }

        FieldMeta<?> reservedField;
        if ((reservedField = nonChild.tryGetField(_MetaBridge.UPDATE_TIME)) != null
                && wrapper.isNullValueParam(reservedField)) {
            throw nullValueErrorForMigration(reservedField);
        }
        if ((reservedField = nonChild.tryGetField(_MetaBridge.VERSION)) != null
                && wrapper.isNullValueParam(reservedField)) {
            throw nullValueErrorForMigration(reservedField);
        }
        if ((reservedField = nonChild.tryGetField(_MetaBridge.VISIBLE)) != null
                && wrapper.isNullValueParam(reservedField)) {
            throw nullValueErrorForMigration(reservedField);
        }

        if (domainTable != nonChild) {
            for (FieldMeta<?> field : nonChild.fieldChain()) {
                if (wrapper.isNullValueParam(field) && !field.nullable()) {
                    throw nullValueErrorForMigration(field);
                }
            }
        }

        for (FieldMeta<?> field : domainTable.fieldChain()) {
            if (wrapper.isNullValueParam(field) && !field.nullable()) {
                throw nullValueErrorForMigration(field);
            }
        }


    }

    abstract void generatorChan(List<FieldMeta<?>> fieldChain, RowWrapper wrapper);


    private void reservedFields(final SingleTableMeta<?> nonChild, final boolean manegeVisible
            , final RowWrapper wrapper) {
        FieldMeta<?> field;

        //1. check id
        field = nonChild.id();
        if (field.generatorType() == null && wrapper.isNullValueParam(field)) {
            throw _Exceptions.nonNullField(field);
        }


        //2. create time
        field = nonChild.getField(_MetaBridge.CREATE_TIME);
        final Class<?> createTimeJavaType;
        createTimeJavaType = field.javaType();
        final Temporal now;
        if (createTimeJavaType == LocalDateTime.class) {
            now = LocalDateTime.now();
        } else if (createTimeJavaType == OffsetDateTime.class) {
            now = OffsetDateTime.now(this.zoneIdSupplier.get());
        } else if (createTimeJavaType == ZonedDateTime.class) {
            now = ZonedDateTime.now(this.zoneIdSupplier.get());
        } else {
            String m = String.format("%s not support java type[%s]", field, createTimeJavaType.getName());
            throw new MetaException(m);
        }
        wrapper.set(field, now);

        //3. update time
        if (!nonChild.immutable()) {
            //update time java type always same with create time
            wrapper.set(nonChild.getField(_MetaBridge.UPDATE_TIME), now);
        }

        //4. version
        field = nonChild.tryGetField(_MetaBridge.VERSION);
        if (field != null) {
            final Class<?> javaType = field.javaType();
            if (javaType == Integer.class) {
                wrapper.set(field, 0);
            } else if (javaType == Long.class) {
                wrapper.set(field, 0L);
            } else if (javaType == BigInteger.class) {
                wrapper.set(field, BigInteger.ZERO);
            } else {
                String m = String.format("%s not support java type[%s]", field, javaType.getName());
                throw new MetaException(m);
            }
        }
        //5. visible
        if (manegeVisible
                && (field = nonChild.tryGetField(_MetaBridge.VISIBLE)) != null
                && wrapper.isNullValueParam(field)) {
            wrapper.set(field, Boolean.TRUE);
        }

    }


    private static CriteriaException nullValueErrorForMigration(FieldMeta<?> field) {
        String m = String.format("%s must be non-null value parameter/literal in migration mode.", field);
        return new CriteriaException(m);
    }


    private static final class DefaultFieldValuesGenerator extends FieldValuesGenerators {

        private final Map<FieldMeta<?>, FieldGenerator> generatorMap;

        private DefaultFieldValuesGenerator(Supplier<ZoneId> zoneIdSupplier
                , Map<FieldMeta<?>, FieldGenerator> generatorMap) {
            super(zoneIdSupplier);
            this.generatorMap = generatorMap;
        }

        @Override
        protected void generatorChan(final List<FieldMeta<?>> fieldChain, final RowWrapper wrapper) {
            final Map<FieldMeta<?>, FieldGenerator> generatorMap = this.generatorMap;
            final ReadWrapper readWrapper = wrapper.readonlyWrapper();
            FieldGenerator generator;
            Object fieldValue;
            for (FieldMeta<?> field : fieldChain) {
                generator = generatorMap.get(field);
                assert generator != null;
                fieldValue = generator.next(field, readWrapper);
                if (!field.javaType().isInstance(fieldValue)) { //must validate
                    throw returnValueError(generator, field, fieldValue);
                }
                wrapper.set(field, fieldValue);
            }

        }


    }//DefaultFieldValuesGenerator


    private static final class MockFieldValuesGenerator extends FieldValuesGenerators {

        private MockFieldValuesGenerator(Supplier<ZoneId> supplier) {
            super(supplier);
        }

        @Override
        void generatorChan(final List<FieldMeta<?>> fieldChain, final RowWrapper wrapper) {
            //mock environment,no-op
        }


    }//MockFieldValuesGenerator


    private static GeneratorException returnValueError(FieldGenerator generator, FieldMeta<?> field, Object value) {
        String m = String.format("%s return value isn't %s instance,for %s"
                , generator.getClass().getName()
                , _ClassUtils.safeClassName(value), field);
        return new GeneratorException(m);
    }


}
