package io.army.criteria;

import io.army.ErrorCode;

@Deprecated
public final class NotFoundRouteException extends CriteriaException {

    public NotFoundRouteException(String format, Object... args) {
        super(ErrorCode.NOT_FOUND_ROUTE_KEY, format, args);
    }

    public NotFoundRouteException(Throwable cause, String format, Object... args) {
        super(ErrorCode.NOT_FOUND_ROUTE_KEY, cause, format, args);
    }
}
