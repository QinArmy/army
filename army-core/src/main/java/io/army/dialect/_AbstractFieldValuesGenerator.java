package io.army.dialect;

import io.army.bean.ObjectWrapper;
import io.army.criteria.CriteriaException;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.TimeUtils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public abstract class _AbstractFieldValuesGenerator implements FieldValuesGenerator {

    @Override
    public final void generate(TableMeta<?> table, ObjectWrapper wrapper, final boolean migrationData) {
        if (!(table instanceof SimpleTableMeta)) {
            discriminatorValue(table, wrapper);
        }
        if (migrationData) {
            checkArmyManagerFields(table, wrapper);
        } else {
            reservedFields(table, wrapper);
            generatorChan(table, wrapper);
        }
    }


    protected abstract ZoneOffset zoneOffset();

    protected abstract void generatorChan(TableMeta<?> table, ObjectWrapper wrapper);


    private void reservedFields(TableMeta<?> table, ObjectWrapper wrapper) {
        final TableMeta<?> domain;
        if (table instanceof ChildTableMeta) {
            domain = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            domain = table;
        }

        final OffsetDateTime now = OffsetDateTime.now(this.zoneOffset());
        createDateTimeValue(domain.getField(_MetaBridge.CREATE_TIME), now, wrapper);
        if (!domain.immutable()) {
            createDateTimeValue(domain.getField(_MetaBridge.UPDATE_TIME), now, wrapper);
        }

        FieldMeta<?> field;
        if (domain.containField(_MetaBridge.VERSION)) {
            field = domain.getField(_MetaBridge.VERSION);
            Class<?> javaType = field.javaType();
            if (javaType == Integer.class) {
                wrapper.set(_MetaBridge.VERSION, 0);
            } else if (javaType == Long.class) {
                wrapper.set(_MetaBridge.VERSION, 0L);
            } else {
                String m = String.format("%s not support java type[%s]", field, javaType.getName());
                throw new MetaException(m);
            }
        }

        if (domain.containField(_MetaBridge.VISIBLE) && wrapper.get(_MetaBridge.VISIBLE) == null) {
            wrapper.set(_MetaBridge.VISIBLE, Boolean.TRUE);
        }

    }


    private void createDateTimeValue(FieldMeta<?> field, OffsetDateTime now, ObjectWrapper wrapper) {
        final Class<?> javaType = field.javaType();
        if (javaType == LocalDateTime.class) {
            wrapper.set(field.fieldName(), now.withOffsetSameInstant(TimeUtils.systemZoneOffset()).toLocalDateTime());
        } else if (javaType == OffsetDateTime.class) {
            wrapper.set(field.fieldName(), now);
        } else if (javaType == ZonedDateTime.class) {
            wrapper.set(field.fieldName(), now.toZonedDateTime());
        } else {
            String m = String.format("%s not support java type[%s]", field, javaType.getName());
            throw new MetaException(m);
        }
    }


    private <E extends Enum<E> & CodeEnum> void discriminatorValue(TableMeta<?> table, ObjectWrapper wrapper) {
        final TableMeta<?> domain;
        if (table instanceof ChildTableMeta) {
            domain = ((ChildTableMeta<?>) table).parentMeta();
        } else if (table instanceof ParentTableMeta) {
            domain = table;
        } else {
            throw new IllegalArgumentException("table error");
        }
        final FieldMeta<?> discriminator;
        discriminator = domain.discriminator();
        final CodeEnum codeEnum;
        codeEnum = CodeEnum.resolve(discriminator.javaType(), table.discriminatorValue());
        if (codeEnum == null) {
            String m = String.format("%s code[%s] no mapping.", discriminator.javaType().getName(), table.discriminatorValue());
            throw new MetaException(m);
        }
        wrapper.set(discriminator.fieldName(), codeEnum);
    }

    private void checkArmyManagerFields(TableMeta<?> table, ObjectWrapper wrapper) {
        if (wrapper.get(_MetaBridge.ID) == null) {
            throw nullValueErrorForMigration(table.id());
        }

        final TableMeta<?> domain;
        if (table instanceof ChildTableMeta) {
            domain = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            domain = table;
        }
        if (wrapper.get(_MetaBridge.CREATE_TIME) == null) {
            throw nullValueErrorForMigration(domain.getField(_MetaBridge.CREATE_TIME));
        }

        if (!domain.immutable() && wrapper.get(_MetaBridge.UPDATE_TIME) == null) {
            throw nullValueErrorForMigration(domain.getField(_MetaBridge.UPDATE_TIME));
        }
        if (domain.containField(_MetaBridge.VERSION) && wrapper.get(_MetaBridge.VERSION) == null) {
            throw nullValueErrorForMigration(domain.getField(_MetaBridge.VERSION));
        }
        if (domain.containField(_MetaBridge.VISIBLE) && wrapper.get(_MetaBridge.VISIBLE) == null) {
            throw nullValueErrorForMigration(domain.getField(_MetaBridge.VISIBLE));
        }

        for (FieldMeta<?> field : table.generatorChain()) {
            if (wrapper.get(field.fieldName()) == null) {
                throw nullValueErrorForMigration(field);
            }
        }
        if (table != domain) {
            for (FieldMeta<?> field : domain.generatorChain()) {
                if (wrapper.get(field.fieldName()) == null) {
                    throw nullValueErrorForMigration(field);
                }
            }
        }


    }// checkArmyManagerFields


    private static CriteriaException nullValueErrorForMigration(FieldMeta<?> field) {
        String m = String.format("%s couldn't be null in migration mode.", field);
        return new CriteriaException(m);
    }


}
