package io.army.criteria.impl;

import io.army.annotation.*;
import io.army.domain.IDomain;
import io.army.generator.PostFieldGenerator;
import io.army.generator.PreFieldGenerator;
import io.army.lang.Nullable;
import io.army.mapping.CodeEnumType;
import io.army.mapping.MappingType;
import io.army.mapping.NameEnumType;
import io.army.mapping._MappingFactory;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.struct.CodeEnum;
import io.army.util.StringUtils;

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

    static final class GeneratorMetaImpl implements GeneratorMeta {

        private final FieldMeta<?, ?> fieldMeta;

        private final Class<?> type;

        private final Map<String, String> params;

        private final String dependPropName;


        private GeneratorMetaImpl(FieldMeta<?, ?> fieldMeta, Class<?> type, Map<String, String> params) {

            this.type = type;
            this.fieldMeta = fieldMeta;

            final Map<String, String> emptyMap = Collections.emptyMap();
            if (params == emptyMap) {
                this.params = params;
            } else {
                this.params = Collections.unmodifiableMap(params);
            }

            if (PreFieldGenerator.class.isAssignableFrom(type)) {
                this.dependPropName = this.params.getOrDefault(PreFieldGenerator.DEPEND_FIELD_NAME, "");
            } else {
                this.dependPropName = "";
            }
        }

        @Override
        public FieldMeta<?, ?> fieldMeta() {
            return fieldMeta;
        }

        @Override
        public Class<?> type() {
            return type;
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


    @Nullable
    static GeneratorMeta columnGeneratorMeta(Field field, FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        if (_MetaBridge.ID.equals(fieldMeta.fieldName()) && fieldMeta.tableMeta() instanceof ChildTableMeta) {
            return null;
        }
        final Generator generator = field.getAnnotation(Generator.class);
        if (generator == null) {
            return null;
        }
        assertManagedByArmyForGenerator(fieldMeta, isDiscriminator);

        final Class<?> generatorClass;
        generatorClass = loadGeneratorClass(fieldMeta, generator.value());

        assertProGenerator(generatorClass, fieldMeta);

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
        if (generatorClass == PostFieldGenerator.class) {
            if (!paramMap.isEmpty()) {
                String m = String.format("%s no param", PostFieldGenerator.class.getName());
                throw new MetaException(m);
            }
        } else if (!PostFieldGenerator.class.isAssignableFrom(generatorClass)) {
            String m = String.format("%s value must be %s or %s", Generator.class.getName()
                    , PostFieldGenerator.class.getName(), PreFieldGenerator.class.getName());
            throw new MetaException(m);
        } else if (paramMap.containsKey(PreFieldGenerator.DEPEND_FIELD_NAME)// assert DEPEND_PROP_NAME value
                && !PreFieldGenerator.class.isAssignableFrom(generatorClass)) {
            String m = String.format("Domain[%s] field[%s] generator cannot have %s value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.fieldName()
                    , PreFieldGenerator.DEPEND_FIELD_NAME);
            throw new MetaException(m);
        }
        return new GeneratorMetaImpl(fieldMeta, generatorClass, paramMap);
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

    static boolean isDiscriminator(final FieldMeta<?, ?> fieldMeta) {
        final Inheritance inheritance = fieldMeta.javaType().getAnnotation(Inheritance.class);
        return inheritance != null
                && fieldMeta.columnName().equalsIgnoreCase(inheritance.value());
    }


    static UpdateMode columnUpdatable(FieldMeta<?, ?> fieldMeta, final Column column, boolean isDiscriminator) {
        final String fieldName = fieldMeta.fieldName();
        final UpdateMode mode;
        if (isDiscriminator
                || _MetaBridge.ID.equals(fieldName)
                || _MetaBridge.CREATE_TIME.equals(fieldName)) {
            mode = UpdateMode.IMMUTABLE;
        } else if (fieldMeta.tableMeta().immutable()) {
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


    static String columnComment(final Column column, FieldMeta<?, ?> fieldMeta, final boolean isDiscriminator) {
        String comment = column.comment().trim();
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


    static String columnDefault(Column column, FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        final String defaultValue = column.defaultValue();
        if (!fieldMeta.nullable()
                && !StringUtils.hasText(defaultValue)
                && !isDiscriminator
                && !_MetaBridge.RESERVED_PROPS.contains(fieldMeta.fieldName())
                && !_MetaBridge.MAYBE_NO_DEFAULT_TYPES.contains(fieldMeta.javaType())) {
            throw new MetaException("%s non-null ,please specified defaultValue() for it.", fieldMeta);
        }
        return defaultValue;
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


    private static String commentManagedByArmy(FieldMeta<?, ?> fieldMeta) {
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
     * @see #columnGeneratorMeta(Field, FieldMeta, boolean)
     */
    private static void assertProGenerator(Class<?> generatorClass, FieldMeta<?, ?> fieldMeta) {
        // assert Generator
        if (!PreFieldGenerator.class.isAssignableFrom(generatorClass)) {
            String m = String.format("Domain[%s] field[%s] generator error, isn't %s"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.fieldName()
                    , PreFieldGenerator.class.getName());
            throw new MetaException(m);
        }
    }

    /**
     * @see #columnGeneratorMeta(Field, FieldMeta, boolean)
     */
    private static void assertManagedByArmyForGenerator(FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        final String fieldName = fieldMeta.fieldName();
        if (_MetaBridge.RESERVED_PROPS.contains(fieldName) && (!_MetaBridge.ID.equals(fieldName) || isDiscriminator)) {
            String m = String.format("Domain[%s].field[%s] must no Generator"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.fieldName());
            throw new MetaException(m);

        }
    }

    /**
     * @see #columnGeneratorMeta(Field, FieldMeta, boolean)
     */
    private static Class<?> loadGeneratorClass(FieldMeta<?, ?> fieldMeta, final String className) {
        if (!StringUtils.hasText(className)) {
            String m = String.format("Domain[%s] field[%s] generator no class name"
                    , fieldMeta.tableMeta().javaType().getName(), fieldMeta.fieldName());
            throw new MetaException(m);
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            String m = String.format("Domain[%s] field[%s] generator class not found."
                    , fieldMeta.tableMeta().javaType().getName(), fieldMeta.fieldName());
            throw new MetaException(m, e);
        }

    }


    /*################################## blow static inner class ##################################*/


}
