package io.army.codec;

import javax.annotation.Nullable;
import java.util.List;

public interface JsonCodec {

    String encode(Object obj);

    @Nullable
    <T> T decode(String json, Class<T> objectClass);

    <T> List<T> decodeList(String json, Class<T> elementClass);

}
