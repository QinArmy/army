package io.army.struct;


import javax.annotation.Nonnull;

/**
 * replace {@link Comparable#compareTo(Object)} return value
 *
 * @see Comparable
 */
public enum CompareResult implements Compare.Comparer, CodeEnum {

    EQUAL(0, "equals") {
        @Override
        public boolean equal() {
            return true;
        }

        @Override
        public boolean lessThan() {
            return false;
        }

        @Override
        public boolean lessEqual() {
            return true;
        }

        @Override
        public boolean greatThan() {
            return false;
        }

        @Override
        public boolean greatEqual() {
            return true;
        }
    },
    LESS(-1, "less than") {
        @Override
        public boolean equal() {
            return false;
        }

        @Override
        public boolean lessThan() {
            return true;
        }

        @Override
        public boolean lessEqual() {
            return true;
        }

        @Override
        public boolean greatThan() {
            return false;
        }

        @Override
        public boolean greatEqual() {
            return false;
        }
    },
    GREAT(1, "great than") {
        @Override
        public boolean equal() {
            return false;
        }

        @Override
        public boolean lessThan() {
            return false;
        }

        @Override
        public boolean lessEqual() {
            return false;
        }

        @Override
        public boolean greatThan() {
            return true;
        }

        @Override
        public boolean greatEqual() {
            return true;
        }
    };


    /**
     * @see Comparable#compareTo(Object)
     */
    public static CompareResult resolve(int compareResult) {
        CompareResult r;
        if (compareResult == 0) {
            r = CompareResult.EQUAL;
        } else if (compareResult > 0) {
            r = CompareResult.GREAT;
        } else {
            r = CompareResult.LESS;
        }
        return r;
    }


    private final int code;

    private final String display;

    CompareResult(int code, @Nonnull String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @Nonnull
    @Override
    public String alias() {
        return display;
    }


}
