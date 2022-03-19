package io.army.bean;

import java.util.Collections;
import java.util.Map;

final class BeanAccessors {

    final Class<?> beanClass;

    final Map<String, ? extends ValueReadAccessor> readerMap;

    final Map<String, ? extends ValueWriteAccessor> writerMap;

    BeanAccessors(final Class<?> beanClass, final Map<String, ? extends ValueReadAccessor> readerMap
            , final Map<String, ? extends ValueWriteAccessor> writerMap) {
        this.beanClass = beanClass;
        if (readerMap.size() == 0) {
            this.readerMap = Collections.emptyMap();
        } else {
            this.readerMap = Collections.unmodifiableMap(readerMap);
        }
        if (writerMap.size() == 0) {
            this.writerMap = Collections.emptyMap();
        } else {
            this.writerMap = Collections.unmodifiableMap(writerMap);
        }
    }


}
