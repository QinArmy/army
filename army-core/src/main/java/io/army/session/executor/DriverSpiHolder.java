package io.army.session.executor;

public interface DriverSpiHolder {

    <T> T getDriverSpi(Class<T> spiClass);


}
