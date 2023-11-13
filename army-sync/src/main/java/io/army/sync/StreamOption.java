package io.army.sync;

import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface StreamOption {

    @Nullable
    Consumer<StreamCommander> streamCommanderConsumer();

    int splitSize();

    /**
     * <p>Currently,known MySQL connector / J support client stream.
     *
     * @return <ul>
     * <li>true (default) : driver read one row and immediately drain to {@link java.util.stream.Stream}</li>
     * <li>false : driver read all rows and drain to {@link java.util.stream.Stream}</li>
     * </ul>
     */
    boolean isPreferClientStream();

    int fetchSize();

    Consumer<ResultStates> stateConsumer();

    static StreamOption defaultOption() {
        throw new UnsupportedOperationException();
    }

}
