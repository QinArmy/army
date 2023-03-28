package io.army.session;

@Deprecated
public class ParamException extends DataAccessException {


    public ParamException(String message) {
        super(message);
    }

    public ParamException(String message, Throwable cause) {
        super(message, cause);
    }

}
