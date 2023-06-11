package io.army.session;

/**
 * @deprecated use {@link VisibleModeException}
 */
@Deprecated
public class NotSupportNonVisibleException extends SessionException {

    public NotSupportNonVisibleException(String message) {
        super(message);
    }
}
