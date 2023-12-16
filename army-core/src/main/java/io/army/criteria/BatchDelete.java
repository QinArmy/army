package io.army.criteria;

/**
 * <p>
 * This interface representing batch primary DELETE statement that don't return result set.
 *
 * @see BatchUpdate
 * @since 0.6.0
 */
public interface BatchDelete extends DeleteStatement, BatchDmlStatement {

}
