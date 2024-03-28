package io.army.util;

import io.army.session.DataAccessException;
import io.army.session.record.DataRecord;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class RowFunctions {

    private RowFunctions() {
        throw new UnsupportedOperationException();
    }


    public static Map<String, Object> hashMap(final DataRecord row) {
        return putColumnToMap(row, _Collections.hashMapForSize(row.getColumnCount()));
    }


    public static Map<String, Object> treeMap(final DataRecord row) {
        return putColumnToMap(row, new TreeMap<>());
    }

    public static Map<String, Object> linkedHashMap(final DataRecord row) {
        return putColumnToMap(row, new LinkedHashMap<>());
    }


    private static Map<String, Object> putColumnToMap(final DataRecord row, final Map<String, Object> map) {
        final int columnCount = row.getColumnCount();

        String label;
        for (int i = 0; i < columnCount; i++) {
            label = row.getColumnLabel(i);
            if (map.putIfAbsent(label, row.get(i)) != null) {
                String m = String.format("column label[%s] duplication", label);
                throw new DataAccessException(m);
            }
        }
        return map;
    }


}
