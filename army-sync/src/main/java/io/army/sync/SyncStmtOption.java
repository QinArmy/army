/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    static SyncStmtOption preferServerPrepare(boolean prefer) {
        return ArmySyncStmtOptions.preferServerPrepare(prefer);
    }

    static Builder builder() {
        return ArmySyncStmtOptions.builder();
    }


    interface Builder extends StmtOption.BuilderSpec<Builder>, StreamOptionBuilderSpec<Builder> {

        SyncStmtOption build();

    }

}
