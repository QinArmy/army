package io.army.bean;

import io.army.util._Collections;

import java.util.Map;

final class BeanAccessors {

    final Class<?> beanClass;

    final Map<String, Class<?>> fieldTypeMap;
    final Map<String, ? extends ValueReadAccessor> readerMap;

    final Map<String, ? extends ValueWriteAccessor> writerMap;

    BeanAccessors(final Class<?> beanClass, Map<String, Class<?>> fieldTypeMap,
                  final Map<String, ? extends ValueReadAccessor> readerMap,
                  final Map<String, ? extends ValueWriteAccessor> writerMap) {
        this.beanClass = beanClass;
        this.fieldTypeMap = _Collections.unmodifiableMap(fieldTypeMap);
        this.readerMap = _Collections.unmodifiableMap(readerMap);
        this.writerMap = _Collections.unmodifiableMap(writerMap);

    }


}
