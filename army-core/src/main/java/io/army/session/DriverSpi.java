package io.army.session;

public enum DriverSpi {

    JDBC,
    JDBD,
    UNKNOWN;

    public static DriverSpi from(String vendor) {
        final DriverSpi spi;
        switch (vendor) {
            case "java.sql":
                spi = JDBC;
                break;
            case "io.jdbd":
                spi = JDBD;
                break;
            default:
                spi = UNKNOWN;
        }
        return spi;
    }

}
