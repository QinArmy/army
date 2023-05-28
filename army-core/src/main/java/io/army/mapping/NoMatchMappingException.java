package io.army.mapping;

import io.army.criteria.CriteriaException;

public final class NoMatchMappingException extends CriteriaException {

    public NoMatchMappingException(String message) {
        super(message);
    }

    public NoMatchMappingException(String message, Throwable cause) {
        super(message, cause);
    }

}
