package io.army;

import io.army.criteria.SQLStatement;

/**
 * Exception thrown when <ul>
 * <li>{@link io.army.criteria.Insert}</li>
 * <li>{@link io.army.criteria.Select}</li>
 * <li>{@link io.army.criteria.Update}</li>
 * <li>{@link io.army.criteria.Delete}</li>
 * </ul>
 * is Illegal .
 */
public class IllegalStatementException extends ArmyRuntimeException {

    public IllegalStatementException(SQLStatement sqlStatement) {
        super(ErrorCode.ILLEGAL_STATEMENT, "SQLStatement[%s] is illegal,deny execute", sqlStatement);
    }

}
