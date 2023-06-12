package io.army.sync;


public final class StreamOptions {


    public static StreamOptions fetchSize(int fetchSize) {
        if (fetchSize < 0) {
            throw fetchSizeError(fetchSize);
        }
        return new StreamOptions(true, fetchSize, 100, false);
    }

    public static Builder builder(int fetchSize) {
        if (fetchSize < 0) {
            throw fetchSizeError(fetchSize);
        }
        return new Builder(fetchSize);
    }


    public final boolean serverStream;

    public final int fetchSize;

    public final int splitSize;

    public final boolean parallel;


    private StreamOptions(boolean serverStream, int fetchSize, int splitSize, boolean parallel) {
        assert fetchSize > 0 && splitSize > 0;
        this.serverStream = serverStream;
        this.fetchSize = fetchSize;
        this.splitSize = splitSize;
        this.parallel = parallel;
    }

    private StreamOptions(Builder builder) {
        this.serverStream = builder.serverStream;
        this.fetchSize = builder.fetchSize;
        this.splitSize = builder.splitSize;
        this.parallel = builder.parallel;
        assert this.fetchSize > 0 && this.splitSize > 0;
    }


    private static IllegalArgumentException fetchSizeError(int fetchSize) {
        return new IllegalArgumentException(String.format("fetchSize[%s] error", fetchSize));
    }


    public static final class Builder {

        private final int fetchSize;

        private boolean serverStream;


        private int splitSize = 100;

        private boolean parallel;

        private Builder(int fetchSize) {
            this.fetchSize = fetchSize;
        }

        public Builder setServerStream(boolean serverStream) {
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


        public StreamOptions build() {
            if (this.splitSize < 0) {
                throw new IllegalArgumentException(String.format("splitSize[%s] error", splitSize));
            }
            return new StreamOptions(this);
        }


    }//Builder


}
