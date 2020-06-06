package io.army.boot;

import io.army.codec.CodecContext;
import io.army.meta.FieldMeta;

import java.util.HashMap;
import java.util.Map;

final class CodecContextImpl implements CodecContext {

    private final Map<FieldMeta<?, ?>, Map<Object, Integer>> fieldFailCountMap = new HashMap<>();

    CodecContextImpl() {
    }

    @Override
    public int failCount(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        Map<Object, Integer> failCountMap = fieldFailCountMap.get(fieldMeta);
        int failCount;
        if (failCountMap == null) {
            failCount = 0;
        } else {
            failCount = failCountMap.getOrDefault(keyTag, 0);
        }
        return failCount;
    }

    @Override
    public void failIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        Map<Object, Integer> failCountMap = fieldFailCountMap.computeIfAbsent(fieldMeta, key -> new HashMap<>());
        int failCount = failCountMap.computeIfAbsent(keyTag, key -> 0);
        failCountMap.put(keyTag, ++failCount);
    }
}
