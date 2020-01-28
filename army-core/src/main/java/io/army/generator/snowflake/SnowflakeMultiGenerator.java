package io.army.generator.snowflake;

import io.army.dialect.SQLDialect;
import io.army.generator.MultiGenerator;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public class SnowflakeMultiGenerator implements MultiGenerator {

    private SnowflakeMultiGenerator(FieldMeta<?, ?> fieldMeta, Map<String,String> paramMap) {

    }

    @Override
    public Object next(FieldMeta<?, ?> fieldMeta, SQLDialect sqlDialect) {
        return null;
    }

    /**
     *
     * @param paramMap  a unmodifiable map
     */
    public static SnowflakeMultiGenerator build(FieldMeta<?, ?> fieldMeta, Map<String,String> paramMap) {
        return null;
    }
}
