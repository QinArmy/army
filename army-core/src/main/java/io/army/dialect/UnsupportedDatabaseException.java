package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class UnsupportedDatabaseException extends ArmyRuntimeException {

    private final String productName;

    private final int majorVersion;

    private final int minorVersion;

    public UnsupportedDatabaseException(String productName, int majorVersion, int minorVersion) {
        super(ErrorCode.NONE, "unsupported database[%s.%s.%s]", productName, majorVersion, minorVersion);
        this.productName = productName;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
    }

    public String getProductName() {
        return this.productName;
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }
}
