package io.army.session;

import javax.annotation.Nullable;

/**
 * <p>Throw(or emit) by driver spi (for example {@code java.sql.Connection} , {@code io.jdbd.session.DatabaseSession} )<br/>
 * when database driver throw {@link Throwable}
 * <p> {@link ServerException} is important sub-class.
 *
 * @see ServerException
 * @since 1.0
 */
public class DriverException extends DataAccessException {

    private final String sqlState;

    private final int vendorCode;

    public DriverException(String message, Throwable cause, String sqlState, int vendorCode) {
        super(message, cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }

    public DriverException(Throwable cause, String sqlState, int vendorCode) {
        super(cause);
        this.sqlState = sqlState;
        this.vendorCode = vendorCode;
    }


    @Nullable
    public final String getSqlState() {
        return this.sqlState;
    }


    public final int getVendorCode() {
        return this.vendorCode;
    }


}
