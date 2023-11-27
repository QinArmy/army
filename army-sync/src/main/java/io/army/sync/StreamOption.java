package io.army.sync;

import io.army.session.StmtOptionSpec;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface StreamOption extends StmtOptionSpec {

    @Nullable
    Consumer<StreamCommander> commanderConsumer();

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


    static StreamOption fetchSize(int value) {
        return ArmyStreamOptions.fetchSize(value);
    }

    static StreamOption splitSize(int value) {
        return ArmyStreamOptions.splitSize(value);
    }

    static StreamOption stateConsumer(Consumer<ResultStates> consumer) {
        return ArmyStreamOptions.stateConsumer(consumer);
    }

    static StreamOption commanderConsumer(Consumer<StreamCommander> consumer) {
        return ArmyStreamOptions.commanderConsumer(consumer);
    }

    static StreamOptionBuilder builder() {
        return ArmyStreamOptions.builder();
    }


    interface StreamOptionBuilderSpec<B> extends OptionBuilderSpec<B> {

        B splitSize(int value);

        B commanderConsumer(Consumer<StreamCommander> consumer);

        B preferClientStream(boolean yes);

    }

    interface StreamOptionBuilder extends StreamOptionBuilderSpec<StreamOptionBuilder> {

        StreamOption build();

    }


}
