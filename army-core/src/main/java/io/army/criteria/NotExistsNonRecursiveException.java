package io.army.criteria;

/**
 * <p>
 * Throw when WITH RECURSIVE clause not exists non-recursive part and try reference recursive cte.
 * * @since 1.p
 */
public final class NotExistsNonRecursiveException extends CriteriaException {


    public NotExistsNonRecursiveException(String message) {
        super(message);
    }


}
