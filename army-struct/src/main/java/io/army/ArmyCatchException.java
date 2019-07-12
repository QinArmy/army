package io.army;

/**
 * created  on 2018/11/25.
 */
public class ArmyCatchException extends Exception implements IArmyExpression {


    private final ErrorCode errorCode;

    public ArmyCatchException(ErrorCode errorCode) {
        super(errorCode.display());
        this.errorCode = errorCode;
    }


    public ArmyCatchException(ErrorCode errorCode, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args));
        this.errorCode = errorCode;
    }

    public ArmyCatchException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(IArmyExpression.createMessage(format, args), cause);
        this.errorCode = errorCode;
    }

    @Override
    public final ErrorCode getErrorCode() {
        return errorCode;
    }
}
