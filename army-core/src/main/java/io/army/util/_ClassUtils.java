package io.army.util;

import io.army.lang.NonNull;
import io.army.modelgen._MetaBridge;
import org.springframework.lang.Nullable;

import java.lang.reflect.Modifier;

/**
 * @since 1.0
 */
public abstract class _ClassUtils extends org.springframework.util.ClassUtils {

    public static final String PUBLISHER_CLASS_NAME = "org.reactivestreams.Publisher";

    public static final String FLUX_CLASS_NAME = "reactor.core.publisher.Flux";

    public static boolean isReactivePresent() {
        return isPresent("io.army.reactive.Session", null);
    }

    public static boolean isSyncPresent() {
        return isPresent("io.army.sync.Session", null);
    }

    public static boolean isPresent(String className, @Nullable ClassLoader classLoader) {
        return org.springframework.util.ClassUtils.isPresent(className, classLoader);
    }


    public static Class<?> loadDomainMetaClass(@NonNull Class<?> entityClass) throws ClassNotFoundException {
        return forName(entityClass.getName() + _MetaBridge.META_CLASS_NAME_SUFFIX, getDefaultClassLoader());
    }


    public static boolean isMatchMetaClass(Class<?> domainClass, Class<?> metaClazz) {
        int modifiers = metaClazz.getModifiers();
        // StaticMetamodel staticMetamodel = metaClazz.getAnnotation(StaticMetamodel.class);
        return Modifier.isAbstract(modifiers)
                && Modifier.isPublic(modifiers)
                && metaClazz.getEnclosingClass() == null
                //  && staticMetamodel != null
                //  && staticMetamodel.value() == domainClass
                && domainClass.getPackage() == metaClazz.getPackage()
                && metaClazz.getSimpleName().endsWith("_")
                && domainClass.getSimpleName().equals(
                metaClazz.getSimpleName().substring(0, metaClazz.getSimpleName().length() - 1))
                ;
    }
}
