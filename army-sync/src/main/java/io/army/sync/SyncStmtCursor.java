package io.army.sync;

import io.army.criteria.Selection;
import io.army.session.Direction;
import io.army.session.Option;
import io.army.session.StmtCursor;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link StmtCursor} that is produced by {@link io.army.criteria.Statement},army know the {@link Selection} list.
 *
 * @see ResultStates#valueOf(Option)
 * @see SyncStmtCursor#SYNC_STMT_CURSOR
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface SyncStmtCursor extends StmtCursor, SyncCursor {

    /**
     * <p>
     * When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : execute postgre DECLARE command.
     * <br/>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    Option<SyncStmtCursor> SYNC_STMT_CURSOR = Option.from("SYNC STMT CURSOR", SyncStmtCursor.class);


    @Nullable
    <R> R next(Class<R> resultClass);

    @Nullable
    <R> R nextObject(Supplier<R> constructor);

    @Nullable
    <R> R fetchOne(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    @Nullable
    <R> R fetchOneObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(Direction direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(Direction direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(Direction direction, long count, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(Direction direction, long count, Supplier<R> constructor, Consumer<ResultStates> consumer);


}
