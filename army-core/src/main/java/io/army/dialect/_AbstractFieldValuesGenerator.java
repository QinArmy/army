package io.army.dialect;

import io.army.bean.ObjectWrapper;
import io.army.criteria.CriteriaException;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util._Exceptions;

import java.math.BigInteger;
import java.time.*;
import java.time.temporal.Temporal;

public abstract class _AbstractFieldValuesGenerator implements _FieldValueGenerator {

    @Override
    public final void generate(final TableMeta<?> table, boolean manegeVisible, final ObjectWrapper wrapper) {
        if (!(table instanceof SimpleTableMeta)) {
            discriminatorValue(table, wrapper);
        }
        reservedFields(table, manegeVisible, wrapper);
        generatorChan(table, wrapper);
    }

    @Override
    public void validate(final TableMeta<?> table, boolean manegeVisible, final ObjectWrapper wrapper) {
        if (!(table instanceof SimpleTableMeta)) {
            discriminatorValue(table, wrapper);
        }
        checkManagerFields(table, manegeVisible, wrapper);
    }


    protected abstract ZoneOffset factoryZoneOffset();

    protected abstract void generatorChan(TableMeta<?> table, ObjectWrapper wrapper);


    private void reservedFields(final TableMeta<?> table, final boolean manegeVisible, final ObjectWrapper wrapper) {
        //1. get non-child
        final TableMeta<?> nonChild;
        if (table instanceof ChildTableMeta) {
            nonChild = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            nonChild = table;
        }

        FieldMeta<?> field;
        Object fieldValue;

        //2. check id
        field = nonChild.id();
        if (field.generatorType() == null && wrapper.get(field.fieldName()) == null) {
            throw _Exceptions.nonNullField(field);
        }

        //3. get now
        final Instant now = Instant.now();
        final ZoneId systemZoneId, factoryZoneId;
        systemZoneId = ZoneId.systemDefault();
        factoryZoneId = factoryZoneOffset();


        //4. create time
        field = nonChild.getField(_MetaBridge.CREATE_TIME);
        fieldValue = createDateTimeValue(field, now, systemZoneId, factoryZoneId);
        wrapper.set(field.fieldName(), fieldValue);

        //5. update time
        if (!nonChild.immutable()) {
            field = nonChild.getField(_MetaBridge.UPDATE_TIME);
            fieldValue = createDateTimeValue(field, now, systemZoneId, factoryZoneId);
            wrapper.set(field.fieldName(), fieldValue);
        }

        //6. version
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
        //7. visible
        if (manegeVisible
                && nonChild.containField(_MetaBridge.VISIBLE)
                && wrapper.get(_MetaBridge.VISIBLE) == null) {
            wrapper.set(_MetaBridge.VISIBLE, Boolean.TRUE);
        }

    }


    private void discriminatorValue(final TableMeta<?> table, final ObjectWrapper wrapper) {
        final TableMeta<?> parent;
        if (table instanceof ChildTableMeta) {
            parent = ((ChildTableMeta<?>) table).parentMeta();
        } else if (table instanceof ParentTableMeta) {
            parent = table;
        } else {
            throw new IllegalArgumentException("table error");
        }
        final FieldMeta<?> discriminator;
        discriminator = parent.discriminator();
        final CodeEnum codeEnum;
        codeEnum = CodeEnum.resolve(discriminator.javaType(), table.discriminatorValue());
        if (codeEnum == null) {
            String m = String.format("%s code[%s] no mapping.", discriminator.javaType().getName()
                    , table.discriminatorValue());
            throw new MetaException(m);
        }
        wrapper.set(discriminator.fieldName(), codeEnum);
    }

    private void checkManagerFields(final TableMeta<?> table, final boolean manegeVisible
            , final ObjectWrapper wrapper) {
        if (wrapper.get(_MetaBridge.ID) == null) {
            throw nullValueErrorForMigration(table.id());
        }

        final TableMeta<?> parent;
        if (table instanceof ChildTableMeta) {
            parent = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            parent = table;
        }
        if (wrapper.get(_MetaBridge.CREATE_TIME) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.CREATE_TIME));
        }

        if (!parent.immutable() && wrapper.get(_MetaBridge.UPDATE_TIME) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.UPDATE_TIME));
        }
        if (parent.containField(_MetaBridge.VERSION) && wrapper.get(_MetaBridge.VERSION) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.VERSION));
        }
        if (manegeVisible && parent.containField(_MetaBridge.VISIBLE) && wrapper.get(_MetaBridge.VISIBLE) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.VISIBLE));
        }

        //TODO 验证这个设计 的合理性
        for (FieldMeta<?> field : table.fieldChain()) {
            if (wrapper.get(field.fieldName()) == null && !field.nullable()) {
                throw nullValueErrorForMigration(field);
            }
        }
        if (table != parent) {
            for (FieldMeta<?> field : parent.fieldChain()) {
                if (wrapper.get(field.fieldName()) == null && !field.nullable()) {
                    throw nullValueErrorForMigration(field);
                }
            }
        }


    }// checkArmyManagerFields


    private static Temporal createDateTimeValue(final FieldMeta<?> field, final Instant now, final ZoneId systemZoneId
            , final ZoneId factoryZoneId) {
        final Class<?> javaType = field.javaType();
        final Temporal temporal;
        if (javaType == LocalDateTime.class) {
            temporal = LocalDateTime.ofInstant(now, systemZoneId);
        } else if (javaType == OffsetDateTime.class) {
            temporal = OffsetDateTime.ofInstant(now, factoryZoneId);
        } else if (javaType == ZonedDateTime.class) {
            temporal = ZonedDateTime.ofInstant(now, factoryZoneId);
        } else {
            String m = String.format("%s not support java type[%s]", field, javaType.getName());
            throw new MetaException(m);
        }
        return temporal;
    }


    private static CriteriaException nullValueErrorForMigration(FieldMeta<?> field) {
        String m = String.format("%s couldn't be null in migration mode.", field);
        return new CriteriaException(m);
    }


}
