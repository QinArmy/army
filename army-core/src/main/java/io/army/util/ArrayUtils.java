package io.army.util;

import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping.UnaryGenericsMapping;

import java.util.*;

public abstract class ArrayUtils {

    protected ArrayUtils() {
        throw new UnsupportedOperationException();
    }

    @NonNull
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Set<T> asSet(@NonNull Collection<T> collection, @Nullable T... e) {
        Set<T> set = new HashSet<>(collection);
        if (e != null) {
            Collections.addAll(set, e);
        }
        return set;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asSet(@Nullable T... e) {
        return asSet(Collections.emptySet(), e);
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@NonNull Collection<T> collection, @Nullable T... e) {
        return Collections.unmodifiableSet(asSet(collection, e));
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> Set<T> asUnmodifiableSet(@Nullable T... e) {
        return asUnmodifiableSet(Collections.emptySet(), e);
    }


    public static <T> List<T> asUnmodifiableList(T t1, T t2) {
        final List<T> list = _Collections.arrayList(2);
        list.add(t1);
        list.add(t2);
        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> asUnmodifiableList(T t1, T t2, T t3) {
        final List<T> list = _Collections.arrayList(3);
        list.add(t1);
        list.add(t2);
        list.add(t3);
        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> asUnmodifiableList(T t1, T t2, T t3, T t4) {
        final List<T> list = _Collections.arrayList(4);
        list.add(t1);
        list.add(t2);
        list.add(t3);
        list.add(t4);
        return Collections.unmodifiableList(list);
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> asUnmodifiableList(T t1, T t2, T t3, T t4, T t5, @Nullable T... rest) {
        final List<T> list;
        if (rest == null) {
            list = _Collections.arrayList(5);
        } else {
            list = _Collections.arrayList(5 + rest.length);
        }

        list.add(t1);
        list.add(t2);
        list.add(t3);
        list.add(t4);

        list.add(t5);
        if (rest != null) {
            Collections.addAll(list, rest);
        }
        return Collections.unmodifiableList(list);
    }


    public static <T> List<T> unmodifiableListFrom(@Nullable T[] array) {
        List<T> list;
        if (array == null || array.length == 0) {
            list = Collections.emptyList();
        } else if (array.length == 1) {
            list = Collections.singletonList(array[0]);
        } else {
            list = _Collections.arrayList(array.length);
            Collections.addAll(list, array);
            list = Collections.unmodifiableList(list);
        }
        return list;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> unmodifiableListOf(T first, T... rest) {
        final List<T> list;
        if (rest.length == 0) {
            list = Collections.singletonList(first);
        } else {
            final List<T> temp = _Collections.arrayList(1 + rest.length);
            temp.add(first);
            Collections.addAll(temp, rest);
            list = Collections.unmodifiableList(temp);
        }
        return list;
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    @NonNull
    public static <T> List<T> asList(@NonNull Collection<T> collection, @Nullable T... addElements) {
        List<T> list;
        int size = collection.size();
        if (addElements != null) {
            size += addElements.length;
        }
        list = _Collections.arrayList(size);
        list.addAll(collection);

        if (addElements != null) {
            Collections.addAll(list, addElements);
        }
        return list;
    }


    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> List<T> asUnmodifiableList(Collection<T> collection, @Nullable T... addElements) {
        if (collection instanceof List
                && (addElements == null || addElements.length == 0)) {
            return Collections.unmodifiableList((List<T>) collection);
        }
        return Collections.unmodifiableList(asList(collection, addElements));
    }

    public static Class<?> underlyingComponent(final Class<?> arrayType) {
        if (!arrayType.isArray()) {
            throw new IllegalArgumentException(String.format("%s isn't array type", arrayType.getName()));
        }

        Class<?> componentType;
        componentType = arrayType.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
        }
        return componentType;

    }

    public static int dimensionOf(final Class<?> arrayType) {
        assert arrayType.isArray();
        int dimension = 1;
        Class<?> componentType;
        componentType = arrayType.getComponentType();
        while (componentType.isArray()) {
            componentType = componentType.getComponentType();
            dimension++;
        }
        return dimension;
    }

    public static int dimensionOfArrayMapping(final Class<?> javaType) {
        final int dimension;
        if (List.class.isAssignableFrom(javaType)) {
            dimension = 1;
        } else {
            dimension = dimensionOf(javaType);
        }
        return dimension;
    }

    public static int dimensionOfType(final MappingType type) {
        final Class<?> clazz;
        clazz = type.javaType();
        final int dimension;
        if (List.class.isAssignableFrom(clazz)) {
            dimension = 1;
        } else if (clazz.isArray()) {
            dimension = dimensionOf(clazz);
        } else {
            String m = String.format("unknown array dimension of %s .", type);
            throw new ClassCastException(m);
        }
        return dimension;
    }

    public static Class<?> underlyingComponentClass(final MappingType type) {
        final Class<?> componentClass;
        if (!(type instanceof MappingType.SqlArrayType)) {
            throw new IllegalArgumentException("non-array mapping");
        } else if (type instanceof UnaryGenericsMapping.ListMapping) {
            componentClass = ((UnaryGenericsMapping.ListMapping<?>) type).genericsType();
        } else {
            componentClass = underlyingComponent(type.javaType());
        }
        return componentClass;
    }

    public static Class<?> arrayClassOf(final Class<?> elementType, final int dimension) {
        if (dimension < 1) {
            throw new IllegalArgumentException("dimension error");
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dimension; i++) {
            builder.append('[');
        }
        if (elementType.isArray()) {
            builder.append(elementType.getName());
        } else if (elementType.isAnonymousClass()) {
            throw new IllegalArgumentException("don't support anonymous class");
        } else if (!(elementType.isPrimitive())) {
            builder.append('L')
                    .append(elementType.getName())
                    .append(';');
        } else if (elementType == int.class) {
            builder.append('I');
        } else if (elementType == long.class) {
            builder.append('J');
        } else if (elementType == boolean.class) {
            builder.append('Z');
        } else if (elementType == byte.class) {
            builder.append('B');
        } else if (elementType == char.class) {
            builder.append('C');
        } else if (elementType == double.class) {
            builder.append('D');
        } else if (elementType == float.class) {
            builder.append('F');
        } else if (elementType == short.class) {
            builder.append('S');
        } else {
            String m = String.format("don't support %s", elementType.getName());
            throw new IllegalArgumentException(m);
        }
        try {
            return Class.forName(builder.toString());
        } catch (ClassNotFoundException e) {
            //no bug, never here
            throw new RuntimeException(e);
        }
    }

    public static Class<?> arrayClassOf(final Class<?> elementType) {
        return arrayClassOf(elementType, 1);
    }


}
