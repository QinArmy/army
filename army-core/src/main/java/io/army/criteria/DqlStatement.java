package io.army.criteria;

import io.army.dialect.Dialect;
import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface representing DQL statement,this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DqlStatement extends Statement {

    @Override
    SimpleStmt mockAsStmt(Dialect dialect, Visible visible);

}
