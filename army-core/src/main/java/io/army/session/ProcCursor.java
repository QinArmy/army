package io.army.session;


import io.army.criteria.Selection;
import io.army.session.record.ResultStates;

/**
 * <p>This interface representing the database cursor that is produced by store procedure,army don't know the {@link Selection} list.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@code  io.army.sync.SyncProcCursor}</li>
 *     <li>{@code io.army.reactive.ReactiveProcCursor}</li>
 * </ul>
 *
 * @see ResultStates#valueOf(ArmyOption)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface ProcCursor extends Cursor {


}
