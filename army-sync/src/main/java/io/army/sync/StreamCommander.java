package io.army.sync;

@FunctionalInterface
public interface StreamCommander {


    /**
     * Request the {@link java.util.stream.Stream} to stop sending data and clean up resources.
     * <p>
     * Data may still be sent to meet previously signalled demand after calling cancel.
     */
    void cancel();

}
