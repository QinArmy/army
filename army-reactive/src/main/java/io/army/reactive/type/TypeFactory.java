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
