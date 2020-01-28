package io.army.generator.snowflake;

import io.army.dialect.SQLDialect;
import io.army.generator.PreMultiGenerator;
import io.army.meta.FieldMeta;

import java.util.Map;

public class SnowflakePreMultiGenerator implements PreMultiGenerator {

    private SnowflakePreMultiGenerator(FieldMeta<?, ?> fieldMeta, Map<String,String> paramMap) {

    }

    @Override
    public Object next(FieldMeta<?, ?> fieldMeta, SQLDialect sqlDialect) {
        return null;
    }

    /**
     *
     * @param paramMap  a unmodifiable map
     */
    public static SnowflakePreMultiGenerator build(FieldMeta<?, ?> fieldMeta, Map<String,String> paramMap) {
        return null;
    }
}
