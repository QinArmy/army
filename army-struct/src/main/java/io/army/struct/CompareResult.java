package io.army.struct;

import org.springframework.lang.NonNull;

/**
 * replace {@link Comparable#compareTo(Object)} return value
 * created  on 2019-02-23.
 *
 * @see Comparable
 */
public enum CompareResult implements Compare.Comparer, CodeEnum {

    EQUAL(0, "equals") {
        @Override
        public boolean eq() {
            return true;
        }

        @Override
        public boolean lt() {
            return false;
        }

        @Override
        public boolean le() {
            return true;
        }

        @Override
        public boolean gt() {
            return false;
        }

        @Override
        public boolean ge() {
            return true;
        }
    },
    LESS(-1, "less than") {
        @Override
        public boolean eq() {
            return false;
        }

        @Override
        public boolean lt() {
            return true;
        }

        @Override
        public boolean le() {
            return true;
        }

        @Override
        public boolean gt() {
            return false;
        }

        @Override
        public boolean ge() {
            return false;
        }
    },
    GREAT(1, "great than") {
        @Override
        public boolean eq() {
            return false;
        }

        @Override
        public boolean lt() {
            return false;
        }

        @Override
        public boolean le() {
            return false;
        }

        @Override
        public boolean gt() {
            return true;
        }

        @Override
        public boolean ge() {
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

    CompareResult(int code, @NonNull String display) {
        this.code = code;
        this.display = display;
    }

    @Override
    public int code() {
        return code;
    }

    @NonNull
    @Override
    public String display() {
        return display;
    }


}
