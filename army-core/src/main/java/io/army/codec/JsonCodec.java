package io.army.codec;

public interface JsonCodec {

    String encode(Object obj);

    Object decode(String json);

}
