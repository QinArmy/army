package io.army.boot.migratioin;

import io.army.sqltype.PostgreDataType;
import io.army.util.ArrayUtils;

import java.util.*;

abstract class PostgreUtils extends ComparatorUtils {

    static Set<PostgreDataType> createIntegerSet() {
        return EnumSet.of(
                PostgreDataType.SMALLINT,
                PostgreDataType.INTEGER,
                PostgreDataType.BIGINT
        );
    }

    static Set<PostgreDataType> createFloatSet() {
        return EnumSet.of(
                PostgreDataType.REAL,
                PostgreDataType.DOUBLE_PRECISION
        );
    }

    static Set<PostgreDataType> createExactNumericSet() {
        return EnumSet.of(
                PostgreDataType.DECIMAL
        );
    }

    static Set<PostgreDataType> createNumericSet() {
        List<PostgreDataType> list = new ArrayList<>();
        list.addAll(Postgre11MetaSchemaComparator.INTEGER_TYPE_SET);
        list.addAll(Postgre11MetaSchemaComparator.FLOAT_TYPE_SET);
        list.addAll(Postgre11MetaSchemaComparator.EXACT_NUMERIC_TYPE_SET);
        return EnumSet.copyOf(list);
    }

    static Map<PostgreDataType, List<String>> createSynonymsMap() {
        Map<PostgreDataType, List<String>> map = new EnumMap<>(PostgreDataType.class);

        map.put(PostgreDataType.SMALLINT, Collections.singletonList("INT2"));
        map.put(PostgreDataType.INTEGER, ArrayUtils.asUnmodifiableList("INT", "INT4"));
        map.put(PostgreDataType.BIGINT, Collections.singletonList("INT8"));
        map.put(PostgreDataType.DECIMAL, Collections.singletonList("NUMERIC"));

        map.put(PostgreDataType.REAL, Collections.singletonList("FLOAT"));
        map.put(PostgreDataType.DOUBLE_PRECISION, Collections.singletonList("FLOAT"));
        map.put(PostgreDataType.CHAR, Collections.singletonList("CHARACTER"));
        map.put(PostgreDataType.VARCHAR, Collections.singletonList("CHARACTER VARYING"));

        map.put(PostgreDataType.TIME_WITHOUT_TIME_ZONE, Collections.singletonList("TIME"));
        map.put(PostgreDataType.TIME_WITH_TIME_ZONE, Collections.singletonList("TIME"));
        map.put(PostgreDataType.TIMESTAMP_WITHOUT_TIME_ZONE, Collections.singletonList("TIMESTAMP"));
        map.put(PostgreDataType.TIMESTAMP_WITH_TIME_ZONE, Collections.singletonList("TIMESTAMP"));

        return Collections.unmodifiableMap(map);
    }
}
