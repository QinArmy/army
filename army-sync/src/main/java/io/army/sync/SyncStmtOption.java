package io.army.sync;

import io.army.session.MultiStmtMode;
import io.army.session.StmtOption;
import io.army.session.record.ResultStates;

import java.util.function.Consumer;

public interface SyncStmtOption extends StmtOption, StreamOption {


    static SyncStmtOption fetchSize(int value) {
        return ArmySyncStmtOptions.fetchSize(value);
    }


    static SyncStmtOption frequency(int value) {
        return ArmySyncStmtOptions.frequency(value);
    }

    static SyncStmtOption splitSize(int value) {
        return ArmySyncStmtOptions.splitSize(value);
    }

    static SyncStmtOption timeoutMillis(int millis) {
        return ArmySyncStmtOptions.timeoutMillis(millis);
    }

    static SyncStmtOption multiStmtMode(MultiStmtMode mode) {
        return ArmySyncStmtOptions.multiStmtMode(mode);
    }

    static SyncStmtOption stateConsumer(Consumer<ResultStates> consumer) {
        return ArmySyncStmtOptions.stateConsumer(consumer);
    }

    static SyncStmtOption commanderConsumer(Consumer<StreamCommander> consumer) {
        return ArmySyncStmtOptions.commanderConsumer(consumer);
    }

    static Builder builder() {
        return ArmySyncStmtOptions.builder();
    }


    interface Builder extends StmtOption.BuilderSpec<Builder>, StreamOptionBuilderSpec<Builder> {

        SyncStmtOption build();

    }

}
