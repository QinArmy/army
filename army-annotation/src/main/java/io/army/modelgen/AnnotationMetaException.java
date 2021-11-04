package io.army.modelgen;

import io.army.ArmyException;

final class AnnotationMetaException extends ArmyException {

    public AnnotationMetaException(String message) {
        super(message);
    }

    public AnnotationMetaException(String message, Throwable cause) {
        super(message, cause);
    }

}
