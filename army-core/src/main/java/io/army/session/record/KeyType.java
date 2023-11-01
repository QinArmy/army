package io.army.session.record;

public enum KeyType {

    PRIMARY_KEY,
    UNIQUE_KEY,

    INDEX_KEY,

    FULL_TEXT_KEY,

    SPATIAL_KEY,

    NONE,

    UNKNOWN;

    public final boolean isUnique() {
        return this == PRIMARY_KEY || this == UNIQUE_KEY;
    }
}
