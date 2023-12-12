package io.army.criteria;

/**
 * <p>
 * Throw when insert {@link io.army.meta.ChildTableMeta} that parent's id is {@link io.army.annotation.GeneratorType#POST} type
 * and with conflict clause(eg: MySQL ON DUPLICATE KEY clause,MySQL REPLACE,Postgre conflict).
 * Because database couldn't return correct parent ids.
*
 * @since 1.0
 */
public final class ErrorChildInsertException extends CriteriaException {

    public ErrorChildInsertException(String message) {
        super(message);
    }

}
