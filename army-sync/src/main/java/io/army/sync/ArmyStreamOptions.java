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

import io.army.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;

abstract class ArmyStreamOptions {

    private ArmyStreamOptions() {
        throw new UnsupportedOperationException();
    }

    static final StreamOption DEFAULT = new DefaultOption();

    static StreamOption fetchSize(final int value) {
        if (value > 0) {
            return builder()
                    .fetchSize(value)
                    .build();
        }
        return DEFAULT;
    }

    static StreamOption splitSize(final int value) {
        if (value > 0) {
            return builder()
                    .splitSize(value)
                    .build();
        }
        return DEFAULT;
    }

    static StreamOption stateConsumer(final @Nullable Consumer<ResultStates> consumer) {
        if (consumer == null) {
            throw new NullPointerException();
        }
        if (consumer != ResultStates.IGNORE_STATES) {
            return builder()
                    .stateConsumer(consumer)
                    .build();
        }
        return DEFAULT;
    }

    static StreamOption commanderConsumer(final @Nullable Consumer<StreamCommander> consumer) {
        if (consumer == null) {
            throw new NullPointerException();
        }

        return builder()
                .commanderConsumer(consumer)
                .build();
    }

    static StreamOption.StreamOptionBuilder builder() {
        return new ArmyOptionBuilder();
    }


    private static final class DefaultOption implements StreamOption {


        @Override
        public boolean isPreferClientStream() {
            return ArmySyncStmtOptions.DEFAULT_PREFER_CLIENT_STREAM;
        }

        @Override
        public int fetchSize() {
            return 0;
        }

        @Override
        public Consumer<ResultStates> stateConsumer() {
            return ResultStates.IGNORE_STATES;
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return null;
        }

        @Override
        public int splitSize() {
            return 0;
        }

    } // DefaultOption


    private static final class ArmyOptionBuilder implements StreamOption.StreamOptionBuilder {

        private int fetchSize;

        private int splitSize;

        private Consumer<ResultStates> stateConsumer = ResultStates.IGNORE_STATES;

        private Consumer<StreamCommander> commanderConsumer;

        private boolean preferClientStream;

        @Override
        public StreamOption.StreamOptionBuilder fetchSize(int value) {
            this.fetchSize = value;
            return this;
        }

        @Override
        public StreamOption.StreamOptionBuilder stateConsumer(Consumer<ResultStates> consumer) {
            this.stateConsumer = consumer;
            return this;
        }

        @Override
        public StreamOption.StreamOptionBuilder splitSize(int value) {
            this.splitSize = value;
            return this;
        }

        @Override
        public StreamOption.StreamOptionBuilder commanderConsumer(Consumer<StreamCommander> consumer) {
            this.commanderConsumer = consumer;
            return this;
        }

        @Override
        public StreamOption.StreamOptionBuilder preferClientStream(boolean yes) {
            this.preferClientStream = yes;
            return this;
        }

        @Override
        public StreamOption build() {
            return new ArmyStreamOption(this);
        }


    } // ArmyOptionBuilder

    private static final class ArmyStreamOption implements StreamOption {

        private final int fetchSize;

        private final int splitSize;

        private final Consumer<ResultStates> stateConsumer;

        private final Consumer<StreamCommander> commanderConsumer;

        private final boolean preferClientStream;

        private ArmyStreamOption(ArmyOptionBuilder builder) {
            this.fetchSize = builder.fetchSize;
            this.splitSize = builder.splitSize;


            this.commanderConsumer = builder.commanderConsumer;
            this.preferClientStream = builder.preferClientStream;

            final Consumer<ResultStates> stateConsumer = builder.stateConsumer;
            if (stateConsumer == null) {
                this.stateConsumer = ResultStates.IGNORE_STATES;
            } else {
                this.stateConsumer = stateConsumer;
            }
        }

        @Override
        public int fetchSize() {
            return this.fetchSize;
        }

        @Override
        public Consumer<ResultStates> stateConsumer() {
            return this.stateConsumer;
        }

        @Nullable
        @Override
        public Consumer<StreamCommander> commanderConsumer() {
            return this.commanderConsumer;
        }

        @Override
        public int splitSize() {
            return this.splitSize;
        }

        @Override
        public boolean isPreferClientStream() {
            return this.preferClientStream;
        }


    } // ArmyStreamOption


}
