package io.army.dialect;

import io.army.bean.ReadWrapper;
import io.army.criteria.CriteriaException;
import io.army.generator.FieldGenerator;
import io.army.generator.GeneratorException;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

import java.math.BigInteger;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Map;

abstract class FieldValuesGenerators implements FieldValueGenerator {


    static FieldValuesGenerators create(@Nullable ZoneOffset zoneOffset
            , Map<FieldMeta<?>, FieldGenerator> generatorMap) {
        return new DefaultFieldValuesGenerator(zoneOffset, generatorMap);
    }

    static FieldValuesGenerators mock() {
        return new MockFieldValuesGenerator();
    }


    private final ZoneOffset zoneOffset;

    private FieldValuesGenerators(@Nullable ZoneOffset zoneOffset) {
        this.zoneOffset = zoneOffset;
    }

    @Override
    public final void generate(final TableMeta<?> domainTable, final boolean manegeVisible
            , final RowWrapper wrapper) {

        if (!(domainTable instanceof SimpleTableMeta)) {
            discriminatorValue(domainTable, wrapper);
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
            discriminatorValue(domainTable, wrapper);
        }

        final TableMeta<?> nonChild;
        if (domainTable instanceof ChildTableMeta) {
            nonChild = ((ChildTableMeta<?>) domainTable).parentMeta();
        } else {
            nonChild = domainTable;
        }
        if (wrapper.get(_MetaBridge.ID) == null) {
            throw nullValueErrorForMigration(nonChild.id());
        }

        if (wrapper.get(_MetaBridge.CREATE_TIME) == null) {
            throw nullValueErrorForMigration(nonChild.getField(_MetaBridge.CREATE_TIME));
        }

        if (!nonChild.immutable() && wrapper.get(_MetaBridge.UPDATE_TIME) == null) {
            throw nullValueErrorForMigration(nonChild.getField(_MetaBridge.UPDATE_TIME));
        }
        if (nonChild.containField(_MetaBridge.VERSION) && wrapper.get(_MetaBridge.VERSION) == null) {
            throw nullValueErrorForMigration(nonChild.getField(_MetaBridge.VERSION));
        }
        if (nonChild.containField(_MetaBridge.VISIBLE) && wrapper.get(_MetaBridge.VISIBLE) == null) {
            throw nullValueErrorForMigration(nonChild.getField(_MetaBridge.VISIBLE));
        }


        if (domainTable != nonChild) {
            for (FieldMeta<?> field : nonChild.fieldChain()) {
                if (wrapper.get(field.fieldName()) == null && !field.nullable()) {
                    throw nullValueErrorForMigration(field);
                }
            }
        }

        for (FieldMeta<?> field : domainTable.fieldChain()) {
            if (wrapper.get(field.fieldName()) == null && !field.nullable()) {
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
        if (field.generatorType() == null && wrapper.get(field.fieldName()) == null) {
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
            now = OffsetDateTime.now(this.zoneOffset == null ? ZoneId.systemDefault() : this.zoneOffset);
        } else if (createTimeJavaType == ZonedDateTime.class) {
            now = ZonedDateTime.now(this.zoneOffset == null ? ZoneId.systemDefault() : this.zoneOffset);
        } else {
            String m = String.format("%s not support java type[%s]", field, createTimeJavaType.getName());
            throw new MetaException(m);
        }
        wrapper.set(_MetaBridge.CREATE_TIME, now);

        //3. update time
        if (!nonChild.immutable()) {
            //update time java type always same with create time
            wrapper.set(_MetaBridge.UPDATE_TIME, now);
        }

        //4. version
        if (nonChild.containField(_MetaBridge.VERSION)) {
            field = nonChild.getField(_MetaBridge.VERSION);
            final Class<?> javaType = field.javaType();
            if (javaType == Integer.class) {
                wrapper.set(_MetaBridge.VERSION, 0);
            } else if (javaType == Long.class) {
                wrapper.set(_MetaBridge.VERSION, 0L);
            } else if (javaType == BigInteger.class) {
                wrapper.set(_MetaBridge.VERSION, BigInteger.ZERO);
            } else {
                String m = String.format("%s not support java type[%s]", field, javaType.getName());
                throw new MetaException(m);
            }
        }
        //5. visible
        if (manegeVisible
                && nonChild.containField(_MetaBridge.VISIBLE)
                && wrapper.get(_MetaBridge.VISIBLE) == null) {
            wrapper.set(_MetaBridge.VISIBLE, Boolean.TRUE);
        }

    }


    private void discriminatorValue(final TableMeta<?> domainTable, final RowWrapper wrapper) {
        final FieldMeta<?> discriminator;
        discriminator = domainTable.discriminator();
        assert discriminator != null;

        final CodeEnum codeEnum;
        codeEnum = CodeEnum.resolve(discriminator.javaType(), domainTable.discriminatorValue());
        if (codeEnum == null) {
            String m = String.format("%s code[%s] no mapping.", discriminator.javaType().getName()
                    , domainTable.discriminatorValue());
            throw new MetaException(m);
        }
        wrapper.set(discriminator, codeEnum);
    }


    private static CriteriaException nullValueErrorForMigration(FieldMeta<?> field) {
        String m = String.format("%s couldn't be null in migration mode.", field);
        return new CriteriaException(m);
    }


    private static final class DefaultFieldValuesGenerator extends FieldValuesGenerators {

        private final Map<FieldMeta<?>, FieldGenerator> generatorMap;

        private DefaultFieldValuesGenerator(@Nullable ZoneOffset zoneOffset
                , Map<FieldMeta<?>, FieldGenerator> generatorMap) {
            super(zoneOffset);
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
                if (!field.javaType().isInstance(fieldValue)) {
                    throw returnValueError(generator, field, fieldValue);
                }
                wrapper.set(field.fieldName(), fieldValue);
            }

        }


    }//DefaultFieldValuesGenerator


    private static final class MockFieldValuesGenerator extends FieldValuesGenerators {

        private MockFieldValuesGenerator() {
            super(null);
        }

        @Override
        void generatorChan(final List<FieldMeta<?>> fieldChain, final RowWrapper wrapper) {
            for (FieldMeta<?> field : fieldChain) {
                wrapper.set(field.fieldName(), null);
            }
        }


    }//MockFieldValuesGenerator


    private static GeneratorException returnValueError(FieldGenerator generator, FieldMeta<?> field, Object value) {
        String m = String.format("%s return value isn't %s instance,for %s"
                , generator.getClass().getName()
                , _ClassUtils.safeClassName(value), field);
        return new GeneratorException(m);
    }


}
