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

package io.army.reactive.type;

import org.reactivestreams.Publisher;

import javax.annotation.Nullable;

abstract class TypeFactory {

    private TypeFactory() {
        throw new UnsupportedOperationException();
    }

    static Blob blobParam(@Nullable Publisher<byte[]> source) {
        if (source == null) {
            throw new NullPointerException("source must non-null");
        }
        return new ArmyBlob(source);
    }

    static Clob clobParam(@Nullable Publisher<String> source) {
        if (source == null) {
            throw new NullPointerException("source must non-null");
        }
        return new ArmyClob(source);
    }


    private static final class ArmyBlob implements Blob {

        private final Publisher<byte[]> source;

        private ArmyBlob(Publisher<byte[]> source) {
            this.source = source;
        }

        @Override
        public Publisher<byte[]> value() {
            return this.source;
        }


    } // ArmyBlob

    private static final class ArmyClob implements Clob {

        private final Publisher<String> source;

        private ArmyClob(Publisher<String> source) {
            this.source = source;
        }

        @Override
        public Publisher<String> value() {
            return this.source;
        }


    } // ArmyClob
}
