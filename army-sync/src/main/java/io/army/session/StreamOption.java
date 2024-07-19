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

package io.army.session;

import io.army.result.ResultStates;

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
