package io.army.sqltype;

public enum MySQLDataType implements SQLDataType {

    BIT(64) {
        @Override
        public int minPrecision() {
            return 1;
        }
    },
    TINYINT(3),

    BOOLEAN(1),

    SMALLINT(5),

    MEDIUMINT(7),

    INT(10),

    BIGINT(19),

    DECIMAL(65) {
        @Override
        public int maxScale() {
            return 30;
        }
    },

    FLOAT(24),

    DOUBLE(53) {
        @Override
        public int minPrecision() {
            return 25;
        }
    },

    DATE(0),

    TIME(6) {
        @Override
        public boolean precisionMatch(int precisionOfField, int columnSize) {
            return timePrecisionMatch(8, precisionOfField, columnSize);
        }
    },

    DATETIME(6) {
        @Override
        public boolean precisionMatch(int precisionOfField, int columnSize) {
            return timePrecisionMatch(19, precisionOfField, columnSize);
        }
    },

    YEAR(0),

    CHAR(255),

    NCHAR(CHAR.maxPrecision),

    VARCHAR((1 << 16) - 1),

    NVARCHAR(VARCHAR.maxPrecision),

    BINARY(CHAR.maxPrecision),

    VARBINARY(VARCHAR.maxPrecision),

    TINYBLOB(255),

    BLOB((1 << 16) - 1),

    MEDIUMBLOB((1 << 24) - 1),

    TINYTEXT(255),

    TEXT((1 << 16) - 1),

    MEDIUMTEXT((1 << 24) - 1);


    private final int maxPrecision;

    MySQLDataType(int maxPrecision) {
        this.maxPrecision = maxPrecision;
    }

    @Override
    public int maxPrecision() {
        return maxPrecision;
    }


    private static boolean timePrecisionMatch(final int basePrecision, final int precisionOfField,
                                              final int precisionOfColumn) throws IllegalArgumentException {
        if (precisionOfField < 0) {
            return true;
        }
        boolean match;

        if (precisionOfField == 0) {
            match = precisionOfColumn == basePrecision;
        } else if (precisionOfField < 7) {
            match = precisionOfColumn == basePrecision + precisionOfField + 1;
        } else {
            throw new IllegalArgumentException("precisionOfField  error");
        }
        return match;
    }

}
