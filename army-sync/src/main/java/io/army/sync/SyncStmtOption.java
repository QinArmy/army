package io.army.sync;

import io.army.session.StmtOption;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface SyncStmtOption extends StmtOption {

    @Nullable
    Consumer<StreamCommander> streamCommanderConsumer();

    int splitSize();


    boolean isPreferClientStream();

}
