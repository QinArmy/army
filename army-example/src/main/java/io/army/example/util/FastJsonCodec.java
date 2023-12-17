package io.army.example.util;

import com.alibaba.fastjson2.JSON;
import io.army.codec.JsonCodec;

import java.util.List;

public final class FastJsonCodec implements JsonCodec {

    public static FastJsonCodec getInstance() {
        return new FastJsonCodec();
    }

    private FastJsonCodec() {
    }

    @Override
    public String encode(Object obj) {
        return JSON.toJSONString(obj);
    }

    @Override
    public <T> T decode(String json, Class<T> objectClass) {
        return JSON.parseObject(json, objectClass);
    }

    @Override
    public <T> List<T> decodeList(String json, Class<T> elementClass) {
        return JSON.parseArray(json, elementClass);
    }


}
