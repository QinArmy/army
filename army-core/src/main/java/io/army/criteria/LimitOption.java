package io.army.criteria;

public final class LimitOption {

    public static LimitOption build(long offset, long rowCount) {
        return new LimitOption(offset, rowCount);
    }


    private final long offset;

    private final long rowCount;

    private LimitOption(long offset, long rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
    }

    public long offset() {
        return this.offset;
    }

    public long rowCount() {
        return this.rowCount;
    }
}
