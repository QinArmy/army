package org.qinarmy.army.meta;

import org.qinarmy.army.ErrorCode;
import org.qinarmy.army.criteria.MetaException;
import org.qinarmy.army.modelgen.SourceCreateUtils;
import org.qinarmy.army.util.ClassUtils;
import org.qinarmy.army.util.ReflectionUtils;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * created  on 2019-02-24.
 */
public abstract class MetaAssert {


    static void assertMetaMatch(@NonNull Class<?> entityClass, @NonNull TableMeta<?> table,
                                @NonNull org.qinarmy.army.meta.Field<?, ?> field)
            throws MetaException {

        if (field.table() != table) {
            throw new MetaException(ErrorCode.META_ERROR, "meta[%s.%s] illegality",
                    table.tableName(), field.fieldName());
        }
        try {
            Class<?> metaClass = ClassUtils.loadEntityMetaClass(entityClass);
            Field propertyField;
            // table part
            propertyField = ReflectionUtils.findField(metaClass, SourceCreateUtils.TABLE_PROPERTY_NAME);
            assertMetaTable(propertyField, table);

            // field part
            propertyField = ReflectionUtils.findField(metaClass, field.propertyName());
            assertMetaFieldOfTable(propertyField);

        } catch (ClassNotFoundException e) {
            throw new MetaException(ErrorCode.META_ERROR, e, "meta[%s.%s] illegality",
                    table.tableName(), field.fieldName());
        }

    }

    private static void assertEntityMetaField(Field metaField) throws MetaException {
        if (metaField == null
                || !Modifier.isPublic(metaField.getModifiers())
                || !Modifier.isStatic(metaField.getModifiers())
                || !Modifier.isFinal(metaField.getModifiers())
        ) {
            throw new MetaException(ErrorCode.META_ERROR, "meta illegality");
        }

    }

    private static void assertMetaTable(Field field, TableMeta<?> table) throws MetaException {
        assertEntityMetaField(field);

        Object propertyValue = ReflectionUtils.getField(field, null);
        if (propertyValue != table) {
            // now field not instantiation
            throw new MetaException(ErrorCode.META_ERROR, "MetaTable[%s]'s value[%s] error",
                    field.getDeclaringClass().getName(), field.getName(), propertyValue);
        }
    }

    private static void assertMetaFieldOfTable(Field field) throws MetaException {
        assertEntityMetaField(field);

        Object propertyValue = ReflectionUtils.getField(field, null);
        if (propertyValue != null) {
            // now field not instantiation
            throw new MetaException(ErrorCode.META_ERROR, "meta[%s.%s]'s  error",
                    field.getDeclaringClass().getName(), field.getName());
        }

    }


}
