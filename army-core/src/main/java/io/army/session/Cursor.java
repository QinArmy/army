package io.army.session;

import io.army.criteria.Selection;

/**
 * <p>This interface representing database cursor.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link StmtCursor},it's produced by statement,army know {@link Selection} list</li>
 *     <li>{@link ProcCursor},it's produced by procedure,army don't know {@link Selection} list</li>
 *     <li>{@code io.army.sync.SyncCursor} blocking cursor</li>
 *     <li>{@code io.army.reactive.ReactiveCursor} reactive cursor</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface Cursor extends CloseableSpec, OptionSpec {

    String name();

    /**
     * Get The {@link Session} that create this instance.
     *
     * @return The {@link Session} that create this instance.
     */
    Session session();


}
