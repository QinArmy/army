package io.army.sync;

import io.army.session.*;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <p>This interface representing blocking {@link NamedCursor}.
 *
 * @see ResultStates#valueOf(Option)
 * @see SyncCursor#SYNC_CURSOR
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 1.0
 */
public interface SyncCursor extends NamedCursor, AutoCloseable {

    /**
     * <p>
     * When this option is supported by {@link ResultStates},this option representing the statement that produce
     * the {@link ResultStates} declare a cursor. For example : execute postgre DECLARE command.
     * <br/>
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
     */
    Option<SyncCursor> SYNC_CURSOR = Option.from("SYNC CURSOR", SyncCursor.class);


    @Nullable
    <R> R next(Class<R> resultClass);

    @Nullable
    <R> R nextObject(Supplier<R> constructor);

    @Nullable
    <R> R nextRecord(Function<CurrentRecord, R> function);

    @Nullable
    <R> R fetchOne(CursorDirection direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    @Nullable
    <R> R fetchOneObject(CursorDirection direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    @Nullable
    <R> R fetchOneRecord(CursorDirection direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(CursorDirection direction, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(CursorDirection direction, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(CursorDirection direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetch(CursorDirection direction, long count, Class<R> resultClass, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchObject(CursorDirection direction, long count, Supplier<R> constructor, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(CursorDirection direction, long count, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    ResultStates move(CursorDirection direction);

    ResultStates move(CursorDirection direction, long count);

}
