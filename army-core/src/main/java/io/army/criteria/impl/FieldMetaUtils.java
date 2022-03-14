package io.army.criteria.impl;

import io.army.annotation.*;
import io.army.domain.IDomain;
import io.army.generator.PreFieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.CodeEnumType;
import io.army.mapping.MappingType;
import io.army.mapping.NameEnumType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.MetaException;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;
import io.army.util._Exceptions;

import java.lang.reflect.Field;
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

        private final String dependPropName;


        private PreGeneratorMetaImpl(FieldMeta<?> fieldMeta, Class<?> javaType, Map<String, String> params) {
            this.javaType = javaType;
            this.fieldMeta = fieldMeta;
            this.params = CollectionUtils.unmodifiableMap(params);

            this.dependPropName = this.params.getOrDefault(PreFieldGenerator.DEPEND_FIELD_NAME, "");
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
        public String dependFieldName() {
            return dependPropName;
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


    static MappingType columnMappingMeta(final TableMeta<?> tableMeta, final Field field
            , final boolean isDiscriminator) {
        final Class<?> mappingClass = getMappingClass(tableMeta, field);

        final Class<?> fieldJavaType = field.getType();
        if (fieldJavaType.isEnum()
                && mappingClass != null
                && mappingClass != CodeEnumType.class
                && mappingClass != NameEnumType.class) {
            String m = String.format("enum must mapping to %s or %s.", CodeEnumType.class.getName()
                    , NameEnumType.class.getName());
            throw new MetaException(m);
        }

        if (isDiscriminator && (!fieldJavaType.isEnum() || !CodeEnum.class.isAssignableFrom(fieldJavaType))) {
            String m = String.format("discriminator java type must mapping to %s", CodeEnumType.class.getName());
            throw new MetaException(m);
        }

        final MappingType mappingType;
        if (mappingClass == null) {
            mappingType = _MappingFactory.getMapping(fieldJavaType);
        } else {
            mappingType = _MappingFactory.getMapping(mappingClass, fieldJavaType);
        }
        return mappingType;

    }

    static boolean isDiscriminator(final FieldMeta<?> fieldMeta) {
        final Inheritance inheritance = fieldMeta.tableMeta().javaType().getAnnotation(Inheritance.class);
        return inheritance != null && fieldMeta.fieldName().equals(inheritance.value());
    }

    static boolean columnInsertable(FieldMeta<?> field, final Column column, boolean isDiscriminator) {
        final boolean insertable;
        final Generator generator;
        generator = field.javaType().getAnnotation(Generator.class);
        if (generator != null) {
            switch (generator.type()) {
                case PRECEDE:
                    insertable = true;
                    break;
                case POST:
                    // child insertable
                    insertable = field.tableMeta().javaType().getAnnotation(DiscriminatorValue.class) != null;
                    break;
                default:
                    throw _Exceptions.unexpectedEnum(generator.type());
            }
        } else {
            insertable = isDiscriminator
                    || _MetaBridge.RESERVED_PROPS.contains(field.fieldName())
                    || column.insertable();
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
            if (!StringUtils.hasText(comment)) {
                comment = commentManagedByArmy(fieldMeta);
            }
        } else if (!StringUtils.hasText(comment)) {
            String m = String.format("Domain[%s] column[%s] isn't reserved properties or discriminator, so must have common"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.columnName());
            throw new MetaException(m);
        }
        return comment;
    }



    /*################################## blow private method ##################################*/

    @Nullable
    private static Class<?> getMappingClass(final TableMeta<?> tableMeta, final Field field) {
        final Mapping mapping = field.getAnnotation(Mapping.class);
        final Class<?> mappingClass;
        if (mapping == null) {
            mappingClass = null;
        } else {
            try {
                mappingClass = Class.forName(mapping.value());
                if (!MappingType.class.isAssignableFrom(mappingClass)) {
                    String m = String.format("%s.%s mapping class isn't %s type.", tableMeta.javaType().getName()
                            , field.getName(), MappingType.class.getName());
                    throw new MetaException(m);
                }
            } catch (ClassNotFoundException e) {
                String m = String.format("%s.value() class[%s] not found.", Mapping.class.getName(), mapping.value());
                throw new MetaException(m, e);
            }
        }
        return mappingClass;
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
                comment = "version for optimistic lock";
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
        if (!StringUtils.hasText(className)) {
            String m = String.format("%s generator no class name", fieldMeta);
            throw new MetaException(m);
        }
        try {
            final Class<?> clazz;
            clazz = Class.forName(className);
            if (PreFieldGenerator.class.isAssignableFrom(clazz)) {
                String m = String.format("%s generator[%s] isn't %s type."
                        , fieldMeta, className, PreFieldGenerator.class.getName());
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
