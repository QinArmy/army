package io.army.criteria.impl;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.criteria.MetaException;
import io.army.domain.IDomain;
import io.army.generator.PostMultiGenerator;
import io.army.generator.PreMultiGenerator;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.GeneratorMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.struct.CodeEnum;
import io.army.util.AnnotationUtils;
import io.army.util.ClassUtils;
import io.army.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @see io.army.meta.FieldMeta
 * @see DefaultFieldMeta
 */
abstract class FieldMetaUtils extends MetaUtils {

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

            if (PreMultiGenerator.class.isAssignableFrom(type)) {
                this.dependPropName = this.params.getOrDefault(PreMultiGenerator.DEPEND_PROP_NAME, "");
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
        public String dependPropName() {
            return dependPropName;
        }

        @Override
        public Map<String, String> params() {
            return params;
        }
    }

    static Column columnMeta(@NonNull Class<? extends IDomain> entityClass, @NonNull Field field) throws MetaException {
        Column column = AnnotationUtils.getAnnotation(field, Column.class);
        if (column == null) {
            throw createNonAnnotationException(entityClass, Column.class);
        }
        return column;
    }


    @Nullable
    static GeneratorMeta columnGeneratorMeta(Field field, FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        if (TableMeta.ID.equals(fieldMeta.propertyName())
                && fieldMeta.tableMeta().parentMeta() != null) {
            return null;
        }
        Generator generator = AnnotationUtils.getAnnotation(field, Generator.class);
        if (generator == null) {
            return null;
        }
        assertManagedByArmyForGenerator(fieldMeta,isDiscriminator);

        Class<?> generatorClass = loadGeneratorClass(fieldMeta,generator.value());

        assertGeneratorPreOrPost(generatorClass, fieldMeta);

        Map<String, String> paramMap;
        Params[] params = generator.params();
        if (params.length == 0) {
            paramMap = Collections.emptyMap();
        } else {
            paramMap = new HashMap<>((int) (params.length / 0.75f));
            for (Params param : params) {
                paramMap.put(param.name(), param.value());
            }
        }
        assertPreGeneratorDependPropName(paramMap, fieldMeta, generatorClass);
        return new GeneratorMetaImpl(fieldMeta, generatorClass, paramMap);
    }



    static MappingType columnMappingType(@NonNull Field field) {
        Mapping mapping = AnnotationUtils.getAnnotation(field, Mapping.class);

        Class<?> mappingClass;
        if (mapping == null) {
            mappingClass = null;
        } else if (mapping.mapping() != AnnotationUtils.getDefaultValue(Mapping.class, "mapping")) {
            mappingClass = mapping.mapping();
        } else {
            try {
                mappingClass = ClassUtils.forName(mapping.value(), ClassUtils.getDefaultClassLoader());
            } catch (ClassNotFoundException e) {
                throw new MetaException(ErrorCode.META_ERROR, "%s.value() not a %s type."
                        , Mapping.class.getName()
                        , MappingType.class.getName()
                );
            }
        }

        MappingFactory mappingFactory = mappingFactory();
        MappingType mappingType;
        if (mappingClass == null) {
            mappingType = mappingFactory.getMapping(field.getType());
        } else {
            mappingType = mappingFactory.getMapping(field.getType(), mappingClass);
        }
        return mappingType;

    }

    static boolean isDiscriminator(FieldMeta<?, ?> fieldMeta) {
        Inheritance inheritance = AnnotationUtils.getAnnotation(fieldMeta.tableMeta().javaType(), Inheritance.class);
        return inheritance != null
                && fieldMeta.fieldName().equalsIgnoreCase(inheritance.value());
    }

    static boolean columnInsertable(String propName, Column column, boolean isDiscriminator) {
        boolean insertable = column.insertable();
        if (TableMeta.VERSION_PROPS.contains(propName)
                || isDiscriminator) {
            insertable = true;
        }
        return insertable;
    }

    static boolean columnUpdatable(String propName, Column column, boolean isDiscriminator) {
        boolean updatable = column.updatable();
        if (TableMeta.ID.equals(propName)
                || TableMeta.CREATE_TIME.equals(propName)
                || isDiscriminator) {
            updatable = false;
        }
        return updatable;
    }


    @NonNull
    static String columnComment(Column column, FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        String comment = column.comment();
        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                || isDiscriminator) {

            if (!StringUtils.hasText(comment)) {
                comment = commentManagedByArmy(fieldMeta);
            }
        } else if (!StringUtils.hasText(comment)) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] column[%s] no comment."
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.fieldName());
        }
        return comment;
    }

    static boolean columnNullable(Column column, FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                || isDiscriminator) {
            if (column.nullable()) {
                throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] columnNullable must be false.",
                        fieldMeta.tableMeta().javaType(),
                        fieldMeta.fieldName()
                );
            }

        }
        return column.nullable();
    }

    static String columnDefault(Column column, FieldMeta<?, ?> fieldMeta) {
        if (!fieldMeta.nullable()
                && String.class != fieldMeta.javaType()
                && !StringUtils.hasText(column.defaultValue())
                && !isManagedByArmy(fieldMeta)) {
            // TODO zoro optimize
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] no defaultValue.",
                    fieldMeta.tableMeta().javaType(),
                    fieldMeta.fieldName()
            );
        }
        return column.defaultValue().trim();
    }





    /*################################## blow private method ##################################*/

    private static MappingFactory mappingFactory() {
        return MappingFactory.build();
    }

    private static String commentManagedByArmy(FieldMeta<?, ?> fieldMeta) {
        String comment = "";
        switch (fieldMeta.propertyName()) {
            case TableMeta.ID:
                comment = "primary key";
                break;
            case TableMeta.CREATE_TIME:
                comment = "create time";
                break;
            case TableMeta.UPDATE_TIME:
                comment = "update time";
                break;
            case TableMeta.VERSION:
                comment = "version for optimistic lock";
                break;
            case TableMeta.VISIBLE:
                comment = "visible for logic delete";
                break;
            default:
                if (fieldMeta.javaType().isEnum()
                        && CodeEnum.class.isAssignableFrom(fieldMeta.javaType())) {
                    comment = "@see " + fieldMeta.javaType().getName();
                }
        }
        return comment;
    }

    private static boolean isManagedByArmy(FieldMeta<?, ?> fieldMeta) {
        Inheritance inheritance = AnnotationUtils.getAnnotation(fieldMeta.tableMeta().javaType(), Inheritance.class);

        return TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                || (inheritance != null
                && inheritance.value().equalsIgnoreCase(fieldMeta.fieldName()))
                ;
    }


    private static void assertPreGeneratorDependPropName(Map<String, String> paramMap, FieldMeta<?, ?> fieldMeta
            , Class<?> generatorClass) {

        // assert DEPEND_PROP_NAME value
        if (paramMap.containsKey(PreMultiGenerator.DEPEND_PROP_NAME)
                && !ClassUtils.isAssignable(PreMultiGenerator.class, generatorClass)) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] prop[%s] generator cannot have %s value"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()
                    , PreMultiGenerator.DEPEND_PROP_NAME);
        }
    }

    private static void assertGeneratorPreOrPost(Class<?> generatorClass, FieldMeta<?, ?> fieldMeta) {
        // assert Generator
        if (ClassUtils.isAssignable(PreMultiGenerator.class, generatorClass)
                && ClassUtils.isAssignable(PostMultiGenerator.class, generatorClass)) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] prop[%s] generator error, cannot be %s then %s"
                    , fieldMeta.tableMeta().javaType().getName()
                    , fieldMeta.propertyName()
                    , PreMultiGenerator.class.getName()
                    , PostMultiGenerator.class.getName());
        }
    }

    private static void assertManagedByArmyForGenerator(FieldMeta<?, ?> fieldMeta, boolean isDiscriminator) {
        if (!TableMeta.ID.equals(fieldMeta.propertyName())) {
            if (TableMeta.VERSION_PROPS.contains(fieldMeta.propertyName())
                    || isDiscriminator) {
                throw new MetaException(ErrorCode.META_ERROR, "Entity[%s].prop[%s] must no Generator"
                        , fieldMeta.tableMeta().javaType().getName()
                        , fieldMeta.propertyName());
            }
        }
    }

    private static  Class<?> loadGeneratorClass(FieldMeta<?, ?> fieldMeta,String className){
        if(!StringUtils.hasText(className)){
            throw new MetaException(ErrorCode.META_ERROR,"entity[%s] prop[%s] generator no class name"
                    ,fieldMeta.tableMeta().javaType().getName(),fieldMeta.propertyName());
        }
        try {
            return ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
        } catch (ClassNotFoundException e) {
            throw new MetaException(ErrorCode.META_ERROR,e,"entity[%s] prop[%s] generator class not found."
                    ,fieldMeta.tableMeta().javaType().getName(),fieldMeta.propertyName());
        }

    }


}
