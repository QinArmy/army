package io.army.sync;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface StreamOption {

    @Nullable
    Consumer<StreamCommander> streamCommanderConsumer();

    int splitSize();


}
