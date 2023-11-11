package io.army.sync;

import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface StreamOption {

    @Nullable
    Consumer<StreamCommander> streamCommanderConsumer();

    int splitSize();

    boolean isPreferClientStream();

    int fetchSize();

    Consumer<ResultStates> stateConsumer();

}
