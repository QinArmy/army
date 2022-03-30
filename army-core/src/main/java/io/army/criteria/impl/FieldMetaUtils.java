package io.army.criteria.impl;

import io.army.annotation.*;
import io.army.domain.IDomain;
import io.army.generator.FieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.CodeEnumType;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @see io.army.meta.FieldMeta
 * @see DefaultFieldMeta
 */
abstract class FieldMetaUtils extends TableMetaUtils {

    private FieldMetaUtils() {

    }


    static final class PreGeneratorMetaImpl implements GeneratorMeta {

        private final FieldMeta<?> fieldMeta;

        private final Class<?> javaType;

        private final Map<String, String> params;


        private PreGeneratorMetaImpl(FieldMeta<?> fieldMeta, Class<?> javaType, Map<String, String> params) {
            this.javaType = javaType;
            this.fieldMeta = fieldMeta;
            this.params = _CollectionUtils.unmodifiableMap(params);
        }

        @Override
        public FieldMeta<?> field() {
            return fieldMeta;
        }

        @Override
        public Class<?> javaType() {
            return this.javaType;
        }

        @Override
        public Map<String, String> params() {
            return params;
        }
    }

    static Column columnMeta(final Class<? extends IDomain> domainClass, final Field field) throws MetaException {
        final Column column = field.getAnnotation(Column.class);
        if (column == null) {
            String m = String.format("Field[%s.%s] isn't annotated by %s."
                    , domainClass.getName(), field.getName(), Column.class.getName());
            throw new MetaException(m);
        }
        return column;
    }

    static void validatePostGenerator(FieldMeta<?> field, Generator generator, boolean isDiscriminator) {
        if (!generator.value().isEmpty() || generator.params().length != 0) {
            String m = String.format("%s config error on %s.", Generator.class.getName(), field);
            throw new MetaException(m);
        }
        if (isDiscriminator) {
            String m = String.format("%s is discriminator,so don't support %s.", field, Generator.class.getName());
            throw new MetaException(m);
        }
        if (!_MetaBridge.ID.equals(field.fieldName())) {
            String m = String.format("%s %s type support only %s field."
                    , Generator.class.getName(), GeneratorType.POST, _MetaBridge.ID);
            throw new MetaException(m);
        }
        if (field.insertable()) {
            String m = String.format("%s insertable error.", field);
            throw new MetaException(m);
        }
        final Class<?> javaType = field.javaType();
        if (javaType != Integer.class && javaType != Long.class && javaType != BigInteger.class) {
            throw _Exceptions.autoIdErrorJavaType((PrimaryFieldMeta<?>) field);
        }
    }

    static GeneratorMeta columnGeneratorMeta(Generator generator, FieldMeta<?> fieldMeta, boolean isDiscriminator) {
        final String fieldName = fieldMeta.fieldName();
        if (isDiscriminator || (!_MetaBridge.ID.equals(fieldName) && _MetaBridge.RESERVED_PROPS.contains(fieldName))) {
            String m = String.format("%s is managed by army ,so must no %s", fieldMeta, Generator.class.getName());
            throw new MetaException(m);
        }
        final Class<?> javaType;
        javaType = loadPreGeneratorClass(fieldMeta, generator.value());

        final Map<String, String> paramMap;
        final Param[] params = generator.params();
        if (params.length == 0) {
            paramMap = Collections.emptyMap();
        } else {
            paramMap = new HashMap<>((int) (params.length / 0.75f));
            for (Param param : params) {
                paramMap.put(param.name(), param.value());
            }
        }
        return new PreGeneratorMetaImpl(fieldMeta, javaType, paramMap);
    }


    static MappingType fieldMappingType(final Field field, final boolean isDiscriminator) {

        final Class<?> fieldJavaType = field.getType();
        final Mapping mapping;
        mapping = field.getAnnotation(Mapping.class);

        final MappingType mappingType;
        if (mapping == null) {
            if (isDiscriminator && !CodeEnum.class.isAssignableFrom(fieldJavaType)) {
                throw discriminatorNotCodeEnum(null, field);
            }
            mappingType = _MappingFactory.getDefault(fieldJavaType);
        } else {
            if (isDiscriminator && CodeEnumType.class.getName().equals(mapping.value())) {
                throw discriminatorNotCodeEnum(mapping, field);
            }
            mappingType = _MappingFactory.map(mapping, field);
        }
        return mappingType;

    }


    static boolean isDiscriminator(final FieldMeta<?> fieldMeta) {
        final Inheritance inheritance = fieldMeta.tableMeta().javaType().getAnnotation(Inheritance.class);
        return inheritance != null && fieldMeta.fieldName().equals(inheritance.value());
    }

    static boolean columnInsertable(FieldMeta<?> field, @Nullable Generator generator
            , final Column column, final boolean isDiscriminator) {
        final boolean insertable;
        if (generator == null) {
            insertable = isDiscriminator
                    || _MetaBridge.RESERVED_PROPS.contains(field.fieldName())
                    || column.insertable();
        } else {
            switch (generator.type()) {
                case PRECEDE:
                    insertable = true;
                    break;
                case POST:
                    // child insertable
                    insertable = field.tableMeta() instanceof ChildTableMeta;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(generator.type());
            }

        }
        return insertable;
    }

    static UpdateMode columnUpdatable(FieldMeta<?> field, final Column column, boolean isDiscriminator) {
        final String fieldName = field.fieldName();
        final UpdateMode mode;
        if (isDiscriminator
                || _MetaBridge.ID.equals(fieldName)
                || _MetaBridge.CREATE_TIME.equals(fieldName)) {
            mode = UpdateMode.IMMUTABLE;
        } else if (field.tableMeta().immutable()) {
            mode = UpdateMode.IMMUTABLE;
        } else if (_MetaBridge.UPDATE_TIME.equals(fieldName)
                || _MetaBridge.VERSION.equals(fieldName)
                || _MetaBridge.VISIBLE.equals(fieldName)) {
            mode = UpdateMode.UPDATABLE;
        } else {
            mode = column.updateMode();
        }
        return mode;
    }


    static String columnComment(final Column column, FieldMeta<?> fieldMeta, final boolean isDiscriminator) {
        String comment = column.comment();
        if (_MetaBridge.RESERVED_PROPS.contains(fieldMeta.fieldName()) || isDiscriminator) {
            if (!_StringUtils.hasText(comment)) {
                comment = commentManagedByArmy(fieldMeta);
            }
        } else if (!_StringUtils.hasText(comment)) {
            String m = String.format("Domain[%s] column[%s] isn't reserved properties or discriminator, so must have common"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.columnName());
            throw new MetaException(m);
        }
        return comment;
    }



    /*################################## blow private method ##################################*/


    private static MetaException discriminatorNotCodeEnum(final @Nullable Mapping mapping, final Field field) {
        final String m;
        if (mapping == null) {
            m = String.format("Discriminator %s.%s type %s don't implements %s."
                    , field.getDeclaringClass().getName(), field.getName()
                    , field.getType().getName(), CodeEnum.class.getName());
        } else {
            m = String.format("Discriminator %s.%s %s.value() isn't %s."
                    , field.getDeclaringClass().getName(), field.getName()
                    , Mapping.class.getName(), CodeEnumType.class.getName());
        }
        return new MetaException(m);
    }


    private static String commentManagedByArmy(FieldMeta<?> fieldMeta) {
        String comment = "";
        switch (fieldMeta.fieldName()) {
            case _MetaBridge.ID:
                comment = "primary key";
                break;
            case _MetaBridge.CREATE_TIME:
                comment = "create time";
                break;
            case _MetaBridge.UPDATE_TIME:
                comment = "update time";
                break;
            case _MetaBridge.VERSION:
                comment = "version that's update counter of row";
                break;
            case _MetaBridge.VISIBLE:
                comment = "visible for logic delete";
                break;
            default:
                if (fieldMeta.javaType().isEnum()) {
                    comment = "@see " + fieldMeta.javaType().getName();
                }
        }
        return comment;
    }


    /**
     * @see #columnGeneratorMeta(Generator, FieldMeta, boolean)
     */
    private static Class<?> loadPreGeneratorClass(FieldMeta<?> fieldMeta, final String className) {
        if (!_StringUtils.hasText(className)) {
            String m = String.format("%s generator no class name", fieldMeta);
            throw new MetaException(m);
        }
        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            if (!FieldGenerator.class.isAssignableFrom(clazz)) {
                String m = String.format("%s generator[%s] isn't %s type."
                        , fieldMeta, className, FieldGenerator.class.getName());
                throw new MetaException(m);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            String m = String.format("%s generator[%s] not found.", fieldMeta, className);
            throw new MetaException(m, e);
        }

    }


    /*################################## blow static inner class ##################################*/


}
