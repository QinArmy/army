package io.army.dialect;

import io.army.bean.ObjectAccessor;
import io.army.bean.ReadWrapper;
import io.army.criteria.CriteriaException;
import io.army.domain.IDomain;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.TimeUtils;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public abstract class _AbstractFieldValuesGenerator implements FieldValueGenerator {

    @Override
    public final void generate(final TableMeta<?> table, final IDomain domain
            , final ObjectAccessor accessor, final ReadWrapper readWrapper) {
        if (!(table instanceof SimpleTableMeta)) {
            discriminatorValue(table, domain, accessor);
        }
        reservedFields(table, domain, accessor);
        generatorChan(table, domain, accessor, readWrapper);
    }

    @Override
    public void validate(final TableMeta<?> table, final IDomain domain, final ObjectAccessor accessor) {
        if (!(table instanceof SimpleTableMeta)) {
            discriminatorValue(table, domain, accessor);
        }
        checkArmyManagerFields(table, domain, accessor);
    }


    protected abstract ZoneOffset zoneOffset();

    protected abstract void generatorChan(TableMeta<?> table, IDomain domain, ObjectAccessor accessor, ReadWrapper readWrapper);


    private void reservedFields(final TableMeta<?> table, final IDomain domain, final ObjectAccessor accessor) {
        final TableMeta<?> parent;
        if (table instanceof ChildTableMeta) {
            parent = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            parent = table;
        }

        final OffsetDateTime now = OffsetDateTime.now(this.zoneOffset());
        createDateTimeValue(parent.getField(_MetaBridge.CREATE_TIME), now, domain, accessor);
        if (!parent.immutable()) {
            createDateTimeValue(parent.getField(_MetaBridge.UPDATE_TIME), now, domain, accessor);
        }

        if (parent.containField(_MetaBridge.VERSION)) {
            final FieldMeta<?> field;
            field = parent.getField(_MetaBridge.VERSION);
            final Class<?> javaType = field.javaType();
            if (javaType == Integer.class) {
                accessor.set(domain, _MetaBridge.VERSION, 0);
            } else if (javaType == Long.class) {
                accessor.set(domain, _MetaBridge.VERSION, 0L);
            } else if (javaType == BigInteger.class) {
                accessor.set(domain, _MetaBridge.VERSION, BigInteger.ZERO);
            } else {
                String m = String.format("%s not support java type[%s]", field, javaType.getName());
                throw new MetaException(m);
            }
        }

        if (parent.containField(_MetaBridge.VISIBLE) && accessor.get(domain, _MetaBridge.VISIBLE) == null) {
            accessor.set(domain, _MetaBridge.VISIBLE, Boolean.TRUE);
        }

    }


    private void createDateTimeValue(final FieldMeta<?> field, final OffsetDateTime now, final IDomain domain
            , final ObjectAccessor accessor) {
        final Class<?> javaType = field.javaType();
        if (javaType == LocalDateTime.class) {
            LocalDateTime value = now.withOffsetSameInstant(TimeUtils.systemZoneOffset()).toLocalDateTime();
            accessor.set(domain, field.fieldName(), value);
        } else if (javaType == OffsetDateTime.class) {
            accessor.set(domain, field.fieldName(), now);
        } else if (javaType == ZonedDateTime.class) {
            accessor.set(domain, field.fieldName(), now.toZonedDateTime());
        } else {
            String m = String.format("%s not support java type[%s]", field, javaType.getName());
            throw new MetaException(m);
        }
    }


    private void discriminatorValue(final TableMeta<?> table, final IDomain domain, final ObjectAccessor accessor) {
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
        accessor.set(domain, discriminator.fieldName(), codeEnum);
    }

    private void checkArmyManagerFields(final TableMeta<?> table, final IDomain domain, final ObjectAccessor accessor) {
        if (accessor.get(domain, _MetaBridge.ID) == null) {
            throw nullValueErrorForMigration(table.id());
        }

        final TableMeta<?> parent;
        if (table instanceof ChildTableMeta) {
            parent = ((ChildTableMeta<?>) table).parentMeta();
        } else {
            parent = table;
        }
        if (accessor.get(domain, _MetaBridge.CREATE_TIME) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.CREATE_TIME));
        }

        if (!parent.immutable() && accessor.get(domain, _MetaBridge.UPDATE_TIME) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.UPDATE_TIME));
        }
        if (parent.containField(_MetaBridge.VERSION) && accessor.get(domain, _MetaBridge.VERSION) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.VERSION));
        }
        if (parent.containField(_MetaBridge.VISIBLE) && accessor.get(domain, _MetaBridge.VISIBLE) == null) {
            throw nullValueErrorForMigration(parent.getField(_MetaBridge.VISIBLE));
        }

        //TODO 验证这个设计 的合理性
        for (FieldMeta<?> field : table.fieldChain()) {
            if (accessor.get(domain, field.fieldName()) == null) {
                throw nullValueErrorForMigration(field);
            }
        }
        if (table != parent) {
            for (FieldMeta<?> field : parent.fieldChain()) {
                if (accessor.get(domain, field.fieldName()) == null) {
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
