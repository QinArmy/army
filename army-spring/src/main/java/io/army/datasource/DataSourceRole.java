package io.army.datasource;

public enum DataSourceRole {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    TIMEOUT("timeout");

    private final String display;

    DataSourceRole(String display) {
        this.display = display;
    }


    @Override
    public String toString() {
        return this.display;
    }
}
