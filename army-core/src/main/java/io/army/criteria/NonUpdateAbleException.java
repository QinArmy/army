package io.army.criteria;

import io.army.ErrorCode;

public class NonUpdateAbleException extends ArmyCriteriaException {

    public NonUpdateAbleException(String format, Object... args) {
        super(ErrorCode.NON_UPDATABLE, format, args);
    }

}
