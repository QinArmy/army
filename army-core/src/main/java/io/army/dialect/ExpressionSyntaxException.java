package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class ExpressionSyntaxException extends ArmyRuntimeException {

    public ExpressionSyntaxException(String format, Object... args) {
        super(ErrorCode.EXP_SYNTAX_ERROR, format, args);
    }
}
