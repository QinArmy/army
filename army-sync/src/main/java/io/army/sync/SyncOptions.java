package io.army.sync;

import io.army.session.Option;
import io.army.session.ResultStates;

public abstract class SyncOptions {

    /**
     * private constructor
     */
    private SyncOptions() {
        throw new UnsupportedOperationException();
    }


    /**
     * <p>
     * When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : execute postgre DECLARE command.
     * <br/>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    public static final Option<SyncCursor> SYNC_CURSOR = Option.from("SYNC CURSOR", SyncCursor.class);


}
