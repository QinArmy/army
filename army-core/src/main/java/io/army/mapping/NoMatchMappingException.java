package io.army.mapping;

import io.army.criteria.CriteriaException;

public class NoMatchMappingException extends CriteriaException {

    public NoMatchMappingException(String message) {
        super(message);
    }

    public NoMatchMappingException(Throwable cause) {
        super(cause);
    }


}
