package io.army;

import io.army.criteria.CriteriaException;

public class DomainUpdateException extends CriteriaException {

    public DomainUpdateException(String format, Object... args) {
        super(ErrorCode.DOMAIN_UPDATE_ERROR, format, args);
    }

}
