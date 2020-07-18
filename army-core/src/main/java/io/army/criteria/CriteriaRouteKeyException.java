package io.army.criteria;

import io.army.ErrorCode;

public final class CriteriaRouteKeyException extends CriteriaException {

    public CriteriaRouteKeyException(String format, Object... args) {
        super(ErrorCode.ROUTE_KEY_ERROR, format, args);
    }

}
