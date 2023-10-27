package io.army.session.executor;


import io.army.session.DataAccessException;
import io.army.session.OptionSpec;

/**
 * <p>This interface representing executor or {@link io.army.stmt.Stmt}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code io.army.sync.executor.SyncStmtExecutor}</li>
 *     <li>{@code io.army.reactive.executor.ReactiveStmtExecutor}</li>
 * </ul>
 * <p><strong>NOTE</strong> : This interface isn't the sub interface of {@link io.army.session.CloseableSpec},
 * so all implementation of methods of this interface don't check whether closed or not,<br/>
 * but {@link io.army.session.Session} need to do that.
 *
 * @see StmtExecutorFactorySpec
 * @since 1.0
 */
public interface StmtExecutor extends OptionSpec {

    String name();

    /**
     * <p>
     * Session identifier(non-unique, for example : database server cluster),probably is following :
     *     <ul>
     *         <li>server process id</li>
     *         <li>server thread id</li>
     *         <li>other identifier</li>
     *     </ul>
     *     <strong>NOTE</strong>: identifier will probably be updated if reconnect.
     * <br/>
     *
     * @return session identifier
     * @throws DataAccessException throw when session have closed.
     */
    long sessionIdentifier() throws DataAccessException;

    boolean inTransaction() throws DataAccessException;

    boolean isSameFactory(StmtExecutor s);

    /**
     * override {@link Object#toString()}
     *
     * @return driver info, contain : <ol>
     * <li>implementation class name</li>
     * <li>{@link #name()}</li>
     * <li>{@link System#identityHashCode(Object)}</li>
     * </ol>
     */
    @Override
    String toString();

}