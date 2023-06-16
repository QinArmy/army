package io.army.criteria.impl;


import io.army.criteria.Selection;
import io.army.mapping.MappingType;
import io.army.sqltype.SqlType;
import io.army.util._Collections;
import io.army.util._Exceptions;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public abstract class SqlTypeUtils {

    private SqlTypeUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return a unmodified list
     */
    public static List<Selection> mapSelectionList(final SqlType[] sqlTypeArray,
                                                   final IntFunction<String> function) {
        final List<Selection> selectionList = new ArrayList<>(sqlTypeArray.length);
        String name;
        for (int i = 0; i < sqlTypeArray.length; i++) {
            name = function.apply(i);
            if (name == null) {
                throw new NullPointerException();
            } else if (!_StringUtils.hasText(name)) {
                throw new IllegalArgumentException("function return error selection name");
            }
            selectionList.add(ArmySelections.forName(name, sqlTypeToMappingType(sqlTypeArray[i])));
        }
        return _Collections.unmodifiableList(selectionList);
    }


    public static MappingType sqlTypeToMappingType(final SqlType sqlType) {
        switch (sqlType.database()) {
            case MySQL:
            case PostgreSQL:
                //TODO
                break;
            default:
                throw _Exceptions.unexpectedEnum((Enum<?>) sqlType);
        }
        throw new UnsupportedOperationException();
    }


}
