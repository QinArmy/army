package io.army.datasource;

public enum DataSourceRole {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    TIMEOUT_SECONDARY("timeoutSecondary");

    public final String display;

    DataSourceRole(String display) {
        this.display = display;
    }

}
