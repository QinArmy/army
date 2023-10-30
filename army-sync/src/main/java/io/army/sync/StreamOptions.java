package io.army.sync;


import javax.annotation.Nullable;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.util.function.Consumer;

@Deprecated
public final class StreamOptions {


    public static StreamOptions fetchSize(int fetchSize) {
        if (fetchSize < 0) {
            throw fetchSizeError(fetchSize);
        }
        return new StreamOptions(fetchSize);
    }

    public static Builder builder(int fetchSize) {
        if (fetchSize < 0) {
            throw fetchSizeError(fetchSize);
        }
        return new Builder(fetchSize);
    }

    public static final StreamOptions LIST_LIKE = new StreamOptions();


    public final Boolean serverStream;

    public final int fetchSize;

    public final int splitSize;

    public final boolean parallel;

    public final Consumer<StreamCommander> commanderConsumer;


    private StreamOptions() {
        this.serverStream = null;
        this.fetchSize = Integer.MAX_VALUE;
        this.splitSize = 0;
        this.parallel = false;
        this.commanderConsumer = null;
    }

    private StreamOptions(int fetchSize) {
        assert fetchSize > 0;
        this.serverStream = Boolean.TRUE;  // must be this.serverStream = Boolean.TRUE not this.serverStream = true
        this.fetchSize = fetchSize;
        this.splitSize = 100;
        this.parallel = false;

        this.commanderConsumer = null;
    }

    private StreamOptions(Builder builder) {
        final Boolean serverStream = builder.serverStream;
        if (serverStream == null) {
            this.serverStream = null;
        } else if (serverStream) {
            // must be this.serverStream = Boolean.TRUE not this.serverStream = serverStream
            this.serverStream = Boolean.TRUE;
        } else {
            // must be this.serverStream = Boolean.FALSE not this.serverStream = serverStream
            this.serverStream = Boolean.FALSE;
        }
        this.fetchSize = builder.fetchSize;
        this.splitSize = builder.splitSize;
        this.parallel = builder.parallel;

        assert this.fetchSize > 0 && this.splitSize > 0;

        this.commanderConsumer = builder.commanderConsumer;
    }


    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        throw new InvalidObjectException("can't deserialize StreamOptions");
    }


    private void readObjectNoData() throws ObjectStreamException {
        throw new InvalidObjectException("can't deserialize StreamOptions");
    }


    private static IllegalArgumentException fetchSizeError(int fetchSize) {
        return new IllegalArgumentException(String.format("fetchSize[%s] error", fetchSize));
    }


    public static final class Builder {

        private final int fetchSize;

        private Boolean serverStream = Boolean.TRUE;


        private int splitSize = 100;

        private boolean parallel;

        private Consumer<StreamCommander> commanderConsumer;

        private Builder(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        public Builder setServerStream(@Nullable Boolean serverStream) {
            this.serverStream = serverStream;
            return this;
        }


        public Builder setSplitSize(int splitSize) {
            this.splitSize = splitSize;
            return this;
        }

        public Builder setParallel(boolean parallel) {
            this.parallel = parallel;
            return this;
        }

        public Builder setCommanderConsumer(Consumer<StreamCommander> commanderConsumer) {
            this.commanderConsumer = commanderConsumer;
            return this;
        }

        public StreamOptions build() {
            if (this.splitSize < 0) {
                throw new IllegalArgumentException(String.format("splitSize[%s] error", splitSize));
            }
            return new StreamOptions(this);
        }


    }//Builder


}
