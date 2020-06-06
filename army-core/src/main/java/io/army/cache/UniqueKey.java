package io.army.cache;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see SessionCache
 */
public final class UniqueKey {

    private final List<String> propNameList;

    private final List<Object> valueList;

    private final int hash;

    public UniqueKey(List<String> propNameList, List<Object> valueList) {
        this.propNameList = Collections.unmodifiableList(propNameList);
        this.valueList = Collections.unmodifiableList(valueList);
        this.hash = Objects.hash(propNameList, valueList);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UniqueKey)) {
            return false;
        }
        UniqueKey key = (UniqueKey) obj;
        return this.propNameList.equals(key.propNameList)
                && this.valueList.equals(key.valueList);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\nUniqueKey properties[");
        int index = 0;
        for (String propName : this.propNameList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(index)
                    .append(":")
                    .append(propName);
            index++;
        }
        builder.append("]\nvalues[");
        index = 0;
        for (Object value : this.valueList) {
            if (index > 0) {
                builder.append(",");
            }
            builder.append(index)
                    .append(":")
                    .append(value);
            index++;
        }
        builder.append("]\n");
        return builder.toString();
    }
}
