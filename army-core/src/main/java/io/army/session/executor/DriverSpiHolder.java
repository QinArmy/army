package io.army.session.executor;

public interface DriverSpiHolder {

    boolean isDriverAssignableTo(Class<?> spiClass);

    <T> T getDriverSpi(Class<T> spiClass);


}
