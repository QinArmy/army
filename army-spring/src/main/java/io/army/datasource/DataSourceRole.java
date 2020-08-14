package io.army.datasource;

public enum DataSourceRole {

    PRIMARY,
    SECONDARY,
    TIMEOUT_SECONDARY;


    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
