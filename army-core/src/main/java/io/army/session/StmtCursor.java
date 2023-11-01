package io.army.session;

import io.army.criteria.Selection;

import java.util.List;

/**
 * <p>This interface representing the database cursor that is produced by {@link io.army.criteria.Statement},army know the {@link Selection} list.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncStmtCursor}</li>
 *     <li>{@code io.army.reactive.ReactiveStmtCursor}</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface StmtCursor extends Cursor {

    List<Selection> selectionList();

    Selection selection(int indexBasedZero);

    Selection selection(String name);


}
