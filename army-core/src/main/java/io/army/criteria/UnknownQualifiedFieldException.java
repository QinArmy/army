package io.army.criteria;

/**
 * <p>
 * Throw when {@link Statement} exists unknown {@link QualifiedField}.
 * * @since 1.0
 */
public final class UnknownQualifiedFieldException extends CriteriaException {

    public UnknownQualifiedFieldException(String format) {
        super(format);
    }


}
