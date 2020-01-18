package io.army.util;

import io.army.modelgen.MetaConstant;
import org.springframework.lang.NonNull;

import javax.persistence.metamodel.StaticMetamodel;
import java.lang.reflect.Modifier;

/**
 * created  on 2019-02-25.
 */
public abstract class ClassUtils extends org.springframework.util.ClassUtils {


    public static Class<?> loadDomainMetaClass(@NonNull Class<?> entityClass) throws ClassNotFoundException {
        return forName(entityClass.getName() + MetaConstant.META_CLASS_NAME_SUFFIX, getDefaultClassLoader());
    }


    public static boolean isMatchMetaClass(Class<?> domainClass, Class<?> metaClazz) {
        int modifiers = metaClazz.getModifiers();
        StaticMetamodel staticMetamodel = metaClazz.getAnnotation(StaticMetamodel.class);
        return Modifier.isAbstract(modifiers)
                && Modifier.isPublic(modifiers)
                && metaClazz.getEnclosingClass() == null
                && staticMetamodel != null
                && staticMetamodel.value() == domainClass
                && domainClass.getPackage() == metaClazz.getPackage()
                && metaClazz.getSimpleName().endsWith("_")
                && domainClass.getSimpleName().equals(
                        metaClazz.getSimpleName().substring(0, metaClazz.getSimpleName().length() - 1))
                ;
    }
}
