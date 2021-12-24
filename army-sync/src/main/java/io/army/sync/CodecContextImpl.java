package io.army.sync;

import io.army.codec.StatementType;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.util._Assert;

import java.util.HashMap;
import java.util.Map;

final class CodecContextImpl implements InnerCodecContext {

    private final Map<FieldMeta<?, ?>, Map<Object, Integer>> encodeFailCountMap = new HashMap<>();

    private final Map<FieldMeta<?, ?>, Map<Object, Integer>> decodeFailCountMap = new HashMap<>();

    private StatementType statementType;

    CodecContextImpl() {
    }

    @Override
    public int encodeFailCount(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        return failCount(fieldMeta, keyTag, this.encodeFailCountMap);
    }

    @Override
    public void encodeFailIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        failIncrement(fieldMeta, keyTag, this.encodeFailCountMap);
    }

    @Override
    public int decodeFailCount(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        return failCount(fieldMeta, keyTag, this.decodeFailCountMap);
    }

    @Override
    public void decodeFailIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag) {
        failIncrement(fieldMeta, keyTag, this.decodeFailCountMap);
    }

    @Override
    public void statementType(@Nullable StatementType statementType) {
        this.statementType = statementType;
    }

    @Override
    public StatementType statementType() {
        _Assert.state(statementType != null, "statementType is null");
        return statementType;
    }

    /*################################## blow private method ##################################*/

    private static int failCount(FieldMeta<?, ?> fieldMeta, Object keyTag
            , Map<FieldMeta<?, ?>, Map<Object, Integer>> fieldFailCountMap) {

        Map<Object, Integer> failCountMap = fieldFailCountMap.get(fieldMeta);
        int failCount;
        if (failCountMap == null) {
            failCount = 0;
        } else {
            failCount = failCountMap.getOrDefault(keyTag, 0);
        }
        return failCount;
    }

    private static void failIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag
            , Map<FieldMeta<?, ?>, Map<Object, Integer>> fieldFailCountMap) {
        Map<Object, Integer> failCountMap = fieldFailCountMap.computeIfAbsent(fieldMeta, key -> new HashMap<>());
        int failCount = failCountMap.computeIfAbsent(keyTag, key -> 0);
        failCountMap.put(keyTag, ++failCount);
    }
}
