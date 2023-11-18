package io.army.sqltype;

import io.army.util._Collections;
import io.army.util._StringUtils;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

abstract class DataTypeFactory {

    private DataTypeFactory() {
        throw new UnsupportedOperationException();
    }


    static DataType typeFrom(String typeName, final boolean caseSensitivity) {
        if (!_StringUtils.hasText(typeName)) {
            throw new IllegalArgumentException("typeName must have text");
        }
        if (!caseSensitivity) {
            typeName = typeName.toUpperCase(Locale.ROOT);
        }
        return ArmyDataType.INSTANCE_MAP.computeIfAbsent(typeName, ArmyDataType.CONSTRUCTOR);
    }


    private static final class ArmyDataType implements DataType {

        private static final ConcurrentMap<String, ArmyDataType> INSTANCE_MAP = _Collections.concurrentHashMap();

        private static final Function<String, ArmyDataType> CONSTRUCTOR = ArmyDataType::new;

        private final String dataTypeName;

        private ArmyDataType(String dataTypeName) {
            this.dataTypeName = dataTypeName;
        }


        @Override
        public String name() {
            return this.dataTypeName;
        }

        @Override
        public String typeName() {
            return this.dataTypeName;
        }

        @Override
        public boolean isArray() {
            return this.dataTypeName.endsWith("[]");
        }

        @Override
        public boolean isUnknown() {
            return false;
        }

        @Override
        public String toString() {
            return String.format("%s[typeName:%s,hash:%s]", getClass().getName(), this.dataTypeName,
                    System.identityHashCode(this));
        }


    }// DatabaseBuildInType
}
