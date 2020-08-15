package io.army.criteria;

public final class LimitOption {

    public static final LimitOption NONE = new LimitOption(-1, -1);

    public static final LimitOption ONE = new LimitOption(-1, 1);

    public static final LimitOption TEN = new LimitOption(-1, 10);

    public static LimitOption build(int offset, int rowCount) {
        LimitOption option;
        if (offset < 0 && rowCount < 0) {
            option = NONE;
        } else if (offset < 0 && rowCount > 0) {
            option = onlyRowCount(rowCount);
        } else {
            option = new LimitOption(offset, rowCount);
        }
        return option;
    }

    private static LimitOption onlyRowCount(int rowCount) {
        LimitOption option;
        switch (rowCount) {
            case 1:
                option = ONE;
                break;
            case 10:
                option = TEN;
                break;
            default:
                option = new LimitOption(-1, rowCount);
        }
        return option;
    }

    private final int offset;

    private final int rowCount;

    private LimitOption(int offset, int rowCount) {
        this.offset = offset;
        this.rowCount = rowCount;
    }

    public int offset() {
        return this.offset;
    }

    public int rowCount() {
        return this.rowCount;
    }
}
