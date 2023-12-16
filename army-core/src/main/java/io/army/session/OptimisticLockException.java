package io.army.session;


import io.army.criteria.Expression;
import io.army.stmt.Stmt;

/**
 * Throw when satisfy following all conditions :
 * <ul>
 *     <li>domain contain version field</li>
 *     <li>{@link io.army.criteria.Statement} WHERE clause contain version predicate with {@link io.army.criteria.TableField#equal(Expression)}</li>
 *     <li>update affectedRows is zero</li>
 * </ul>
 *
 * @see Stmt#hasOptimistic()
 * @since 0.6.0
 */
public final class OptimisticLockException extends DataAccessException {


    public OptimisticLockException(String message) {
        super(message);
    }

}
