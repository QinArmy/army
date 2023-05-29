package io.army.mapping;

import java.util.Map;
import java.util.function.Supplier;

public interface DualGenericsMapping<T, U> extends MappingType.GenericsMappingType {

    Class<T> firstGenericsType();

    Class<U> secondGenericsType();


    interface MapMapping<K, V> extends DualGenericsMapping<K, V> {

        Supplier<Map<K, V>> mapConstructor();

    }


}
