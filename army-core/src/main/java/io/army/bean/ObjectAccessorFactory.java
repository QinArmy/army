package io.army.bean;


import io.army.lang.Nullable;
import io.army.proxy.ArmyProxy;

import java.lang.ref.SoftReference;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @since 1.0
 */
public abstract class ObjectAccessorFactory {

    private ObjectAccessorFactory() {
        throw new UnsupportedOperationException();
    }

    private static final byte WRITE_METHOD = 1;

    private static final byte READ_METHOD = 2;

    private static final ConcurrentMap<Class<?>, BeanAccessorsReference> ACCESSOR_CACHE = new ConcurrentHashMap<>();

    public static ObjectAccessor forBean(Class<?> beanClass) {
        while (ArmyProxy.class.isAssignableFrom(beanClass)) {
            beanClass = beanClass.getSuperclass();
        }
        return new BeanWriterAccessor(getBeanAccessors(beanClass));
    }

    public static ReadAccessor readOnlyFromInstance(final Object instance) {
        final ReadAccessor accessor;
        if (instance instanceof Map) {
            accessor = MapReadAccessor.INSTANCE;
        } else {
            accessor = forBean(instance.getClass());
        }
        return accessor;
    }

    public static <T> T createBean(final Constructor<T> constructor) {
        try {
            return constructor.newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            String m = String.format("%s don't declared public default constructor."
                    , constructor.getDeclaringClass().getName());
            throw new ObjectAccessException(m, e);
        }
    }

    public static <T> Constructor<T> getConstructor(final Class<T> beanClass) {
        try {
            return beanClass.getConstructor();
        } catch (NoSuchMethodException e) {
            String m = String.format("%s don't declared public default constructor."
                    , beanClass.getName());
            throw new ObjectAccessException(m, e);
        }
    }

    public static <T> Constructor<T> getPairConstructor(final Class<T> beanClass) {
        try {
            return beanClass.getConstructor(Object.class, Object.class);
        } catch (NoSuchMethodException e) {
            String m = String.format("%s don't declared public %s constructor."
                    , beanClass.getName(), PairBean.class.getName());
            throw new ObjectAccessException(m, e);
        }
    }

    public static <T> T createPair(Constructor<T> constructor, @Nullable Object first, @Nullable Object second) {
        try {
            return constructor.newInstance(first, second);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            String m = String.format("%s don't declared public default constructor."
                    , constructor.getDeclaringClass().getName());
            throw new ObjectAccessException(m, e);
        }
    }


    private static BeanAccessors getBeanAccessors(final Class<?> beanClass) {
        final BeanAccessorsReference reference;
        reference = ACCESSOR_CACHE.get(beanClass);
        BeanAccessors accessors = null;
        if (reference != null) {
            accessors = reference.get();
        }
        if (accessors != null) {
            return accessors;
        }
        if (FieldAccessBean.class.isAssignableFrom(beanClass)) {
            accessors = createFieldAccessorPair(beanClass);
        } else {
            accessors = createMethodAccessors(beanClass);
        }
        ACCESSOR_CACHE.put(beanClass, new BeanAccessorsReference(accessors));
        return accessors;
    }


    private static BeanAccessors createMethodAccessors(final Class<?> beanClass) {
        final Map<String, ValueReadAccessor> readerMap = new HashMap<>();
        final Map<String, ValueWriteAccessor> writerMap = new HashMap<>();

        String fieldName, methodName;
        int modifiers;
        ValueReadAccessor readAccessor;
        ValueWriteAccessor writeAccessor;
        int methodType;
        for (Class<?> clazz = beanClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                methodName = method.getName();
                if (methodName.startsWith("set")) {
                    if (method.getParameterCount() != 1) {
                        continue;
                    }
                    methodType = WRITE_METHOD;
                } else if (methodName.startsWith("get")) {
                    if (method.getParameterCount() != 0 || method.getReturnType() == void.class) {
                        continue;
                    }
                    methodType = READ_METHOD;
                } else {
                    continue;
                }
                modifiers = method.getModifiers();
                if (!Modifier.isPublic(modifiers)
                        || Modifier.isStatic(modifiers)
                        || methodName.length() < 4) {
                    continue;
                }
                if (methodName.length() == 4) {
                    fieldName = String.valueOf(Character.toLowerCase(methodName.charAt(3)));
                } else {
                    fieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                }
                switch (methodType) {
                    case WRITE_METHOD: {
                        writeAccessor = method::invoke;
                        writerMap.putIfAbsent(fieldName, writeAccessor);
                    }
                    break;
                    case READ_METHOD: {
                        readAccessor = method::invoke;
                        readerMap.putIfAbsent(fieldName, readAccessor);
                    }
                    break;
                    default:
                        throw new IllegalStateException("unknown method type.");
                }

            }


        }
        return new BeanAccessors(beanClass, readerMap, writerMap);
    }

    private static BeanAccessors createFieldAccessorPair(final Class<?> beanClass) {
        final Map<String, FieldReader> readerMap = new HashMap<>();
        final Map<String, FieldWriter> writerMap = new HashMap<>();

        int modifiers;
        String fieldName;
        FieldReader fieldReader;
        FieldWriter fieldWriter;
        for (Class<?> clazz = beanClass; clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (!FieldAccessBean.class.isAssignableFrom(clazz)) {
                break;
            }
            for (Field field : clazz.getDeclaredFields()) {
                modifiers = field.getModifiers();
                if (!Modifier.isPublic(modifiers)
                        || Modifier.isStatic(modifiers)) {
                    continue;
                }
                fieldName = field.getName();

                fieldReader = field::get;
                readerMap.putIfAbsent(fieldName, fieldReader);

                fieldWriter = field::set;
                writerMap.putIfAbsent(fieldName, fieldWriter);
            }

        }
        return new BeanAccessors(beanClass, readerMap, writerMap);
    }


    private static final class BeanAccessorsReference extends SoftReference<BeanAccessors> {

        private BeanAccessorsReference(BeanAccessors accessors) {
            super(accessors);
        }

        @Override
        public void clear() {
            final BeanAccessors accessors;
            accessors = get();
            super.clear();
            if (accessors != null) {
                ACCESSOR_CACHE.remove(accessors.beanClass, this);
            }

        }

    }//AccessorReference


}
