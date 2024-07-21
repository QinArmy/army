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

import io.army.option.MultiStmtMode;
import io.army.result.ResultStates;

import java.util.function.Consumer;

public interface ReactiveStmtOption extends StmtOption {

    static ReactiveStmtOption defaultOption() {
        return ReactiveStmtOptions.DEFAULT;
    }


    static ReactiveStmtOption fetchSize(int value) {
        return ReactiveStmtOptions.fetchSize(value);
    }


    static ReactiveStmtOption frequency(int value) {
        return ReactiveStmtOptions.frequency(value);
    }

    static ReactiveStmtOption timeoutMillis(int millis) {
        return ReactiveStmtOptions.timeoutMillis(millis);
    }

    static ReactiveStmtOption multiStmtMode(MultiStmtMode mode) {
        return ReactiveStmtOptions.multiStmtMode(mode);
    }

    static ReactiveStmtOption stateConsumer(Consumer<ResultStates> consumer) {
        return ReactiveStmtOptions.stateConsumer(consumer);
    }

    static ReactiveStmtOption preferServerPrepare(boolean prefer) {
        return ReactiveStmtOptions.preferServerPrepare(prefer);
    }

    static Builder builder() {
        return ReactiveStmtOptions.builder();
    }


    interface Builder extends StmtOption.BuilderSpec<Builder> {

        ReactiveStmtOption build();

    }


}
