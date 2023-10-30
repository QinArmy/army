package io.army;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;

/**
 * @since 1.0
 */
@Deprecated
public interface IArmyExpression {

    @Nonnull
    ErrorCode getErrorCode();

    /**
     * @see Throwable#getMessage()
     */
    @Nullable
    String getMessage();

    /**
     * @see Throwable#getLocalizedMessage()
     */
    @Nullable
    String getLocalizedMessage();

    /**
     * @see Throwable#getCause() ()
     */
    @Nullable
    Throwable getCause();

    /**
     * @see Throwable#printStackTrace(PrintWriter)
     */
    void printStackTrace(PrintWriter s);

    /**
     * @see Throwable#printStackTrace()
     */
    void printStackTrace();


    static String createMessage(@Nullable String format, @Nullable Object... args) {
        String msg;
        if (format != null && args != null && args.length > 0) {
            msg = String.format(format, args);
        } else {
            msg = format;
        }
        return msg;
    }


}
