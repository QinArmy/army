package io.army.datasource;

public enum DataSourceRole {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    TIMEOUT_SECONDARY("timeout.secondary");

    private final String tag;

    DataSourceRole(String tag) {
        this.tag = tag;
    }


    @Override
    public final String toString() {
        return this.tag;
    }


}
