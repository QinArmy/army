package io.army.codec;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface JsonCodec {

    String encode(Object obj);

    Object decode(String json, Class<?> objectClass);

    <T> List<T> decodeList(String json, Class<T> elementClass, Supplier<List<T>> listConstructor);

    <T> Set<T> decodeSet(String json, Class<T> elementClass, Supplier<Set<T>> setConstructor);

}
