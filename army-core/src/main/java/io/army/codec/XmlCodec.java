package io.army.codec;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface XmlCodec {

    String encode(Object obj);

    <T> T decode(String xml, Class<T> objectClass);

    <T> List<T> decodeList(String xml, Class<T> elementClass, Supplier<List<T>> listConstructor);

    <T> Set<T> decodeSet(String xml, Class<T> elementClass, Supplier<Set<T>> setConstructor);

}
