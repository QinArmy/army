package io.army.criteria;

import io.army.ArmyException;

/**
 *
 */
public class CriteriaException extends ArmyException {


    public CriteriaException(String format) {
        super(format);
    }

    public CriteriaException(Throwable cause) {
        super(cause);
    }

    public CriteriaException(String format, Throwable cause) {
        super(format, cause);
    }


}
