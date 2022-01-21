package io.army.boot.migratioin;

import io.army.sqltype.PostgreType;
import io.army.util.ArrayUtils;

import java.util.*;

abstract class PostgreUtils extends ComparatorUtils {

    static Set<PostgreType> createIntegerSet() {
        return EnumSet.of(
                PostgreType.SMALLINT,
                PostgreType.INTEGER,
                PostgreType.BIGINT
        );
    }

    static Set<PostgreType> createFloatSet() {
        return EnumSet.of(
                PostgreType.REAL
                //PostgreDataType.DOUBLE_PRECISION
        );
    }

    static Set<PostgreType> createExactNumericSet() {
        return EnumSet.of(
                PostgreType.DECIMAL
        );
    }

    static Set<PostgreType> createNumericSet() {
        List<PostgreType> list = new ArrayList<>();
        list.addAll(Postgre11MetaSchemaComparator.INTEGER_TYPE_SET);
        list.addAll(Postgre11MetaSchemaComparator.FLOAT_TYPE_SET);
        list.addAll(Postgre11MetaSchemaComparator.EXACT_NUMERIC_TYPE_SET);
        return EnumSet.copyOf(list);
    }

    static Map<PostgreType, List<String>> createSynonymsMap() {
        Map<PostgreType, List<String>> map = new EnumMap<>(PostgreType.class);

        map.put(PostgreType.SMALLINT, Collections.singletonList("INT2"));
        map.put(PostgreType.INTEGER, ArrayUtils.asUnmodifiableList("INT", "INT4"));
        map.put(PostgreType.BIGINT, Collections.singletonList("INT8"));
        map.put(PostgreType.DECIMAL, Collections.singletonList("NUMERIC"));

        map.put(PostgreType.REAL, Collections.singletonList("FLOAT"));
        // map.put(PostgreDataType.DOUBLE_PRECISION, Collections.singletonList("FLOAT"));
        map.put(PostgreType.CHAR, Collections.singletonList("CHARACTER"));
        map.put(PostgreType.VARCHAR, Collections.singletonList("CHARACTER VARYING"));

        // map.put(PostgreDataType.TIME_WITHOUT_TIME_ZONE, Collections.singletonList("TIME"));
        // map.put(PostgreDataType.TIME_WITH_TIME_ZONE, Collections.singletonList("TIME"));
        //map.put(PostgreDataType.TIMESTAMP_WITHOUT_TIME_ZONE, Collections.singletonList("TIMESTAMP"));
        // map.put(PostgreDataType.TIMESTAMP_WITH_TIME_ZONE, Collections.singletonList("TIMESTAMP"));

        return Collections.unmodifiableMap(map);
    }
}
