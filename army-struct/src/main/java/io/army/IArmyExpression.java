package io.army;

import io.army.lang.Nullable;
import org.springframework.lang.NonNull;

import java.io.PrintWriter;

/**
 * created  on 2018/11/25.
 */
public interface IArmyExpression {

    @NonNull
    ErrorCode getErrorCode();

    /**
     * @see Throwable#getMessage()
     */
    @Nullable
    String getMessage();

    /**
     * @see Throwable#getLocalizedMessage()
     */
    String getLocalizedMessage();

    /**
     * @see Throwable#getCause() ()
     */
    Throwable getCause();

    /**
     * @see Throwable#printStackTrace(PrintWriter)
     */
    void printStackTrace(PrintWriter s);

    /**
     * @see Throwable#printStackTrace()
     */
    void printStackTrace();


    static String createMessage(@NonNull String format, Object... args) {
        String msg;
        if (format != null && args != null && args.length > 0) {
            msg = String.format(format, args);
        } else {
            msg = format;
        }
        return msg;
    }


}
