package io.army.sync;

import io.army.criteria.Selection;
import io.army.session.ArmyOption;
import io.army.session.ProcCursor;
import io.army.session.record.ResultStates;


/**
 * <p>This interface representing blocking {@link ProcCursor} that is produced by store procedure,army don't know the {@link Selection} list.
 *
 * @see ResultStates#valueOf(ArmyOption)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface SyncProcCursor extends ProcCursor, SyncCursor {

    /**
     * <p>
     * When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : CALL procedure
     * <br/>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    ArmyOption<SyncProcCursor> SYNC_PROC_CURSOR = ArmyOption.from("SYNC PROC CURSOR", SyncProcCursor.class);


}
