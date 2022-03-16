package io.army;

import io.army.lang.Nullable;

/**
 *
 */
public class ArmyRuntimeException extends RuntimeException implements IArmyExpression {

    private static final long serialVersionUID = -3428509683218688609L;

    private ErrorCode errorCode;

    @Deprecated
    public ArmyRuntimeException(ErrorCode errorCode) {
        super(errorCode.display());
        this.errorCode = errorCode;
    }

    @Deprecated
    public ArmyRuntimeException(ErrorCode errorCode, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args));
        this.errorCode = errorCode;
    }

    @Deprecated
    public ArmyRuntimeException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args), cause);
        this.errorCode = errorCode;
    }

    public ArmyRuntimeException(String message) {
        super(message);
        this.errorCode = ErrorCode.NONE;
    }

    public ArmyRuntimeException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public ArmyRuntimeException(Throwable cause, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args), cause);
        this.errorCode = ErrorCode.NONE;
    }

    @Override
    public final ErrorCode getErrorCode() {
        return errorCode;
    }
}
