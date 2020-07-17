package io.army.sharding;

/**
 * This is an iconic interface. This interface implementation can route to a sharding.
 */
public interface Route {

    String convertToSuffix(int tableIndex);

}
