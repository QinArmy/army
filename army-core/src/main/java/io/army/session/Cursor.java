package io.army.session;

import io.army.criteria.Selection;

import java.util.List;

/**
 * <p>This interface representing database cursor.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncCursor}</li>
 *     <li>{@code io.army.reactive.ReactiveCursor}</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface Cursor extends CloseableSpec, OptionSpec {

    String name();

    List<Selection> selectionList();

    Selection selection(int indexBasedZero);

    Selection selection(String name);

    /**
     * Get The {@link Session} that create this instance.
     *
     * @return The {@link Session} that create this instance.
     */
    Session session();


}
