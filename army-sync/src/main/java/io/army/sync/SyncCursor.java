package io.army.sync;

import io.army.criteria.Selection;
import io.army.session.Cursor;
import io.army.session.Direction;
import io.army.session.Option;
import io.army.session.record.CurrentRecord;
import io.army.session.record.ResultItem;
import io.army.session.record.ResultStates;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;


/**
 * <p>This interface representing blocking {@link Cursor}.
 * <p>This interface is base interface of following :
 * <ul>
 *     <li>{@link SyncStmtCursor},it's produced by statement,army know {@link Selection} list</li>
 *     <li>{@link SyncProcCursor},it's produced by procedure,army don't know {@link Selection} list</li>
 * </ul>
 *
 * @see ResultStates#valueOf(Option)
 * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">PostgreSQL DECLARE</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-fetch.html">PostgreSQL FETCH</a>
 * @since 0.6.0
 */
public interface SyncCursor extends Cursor, AutoCloseable {


    @Override
    SyncSession session();


    @Nullable
    <R> R nextRecord(Function<CurrentRecord, R> function);

    @Nullable
    <R> R fetchOneRecord(Direction direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(Direction direction, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    <R> Stream<R> fetchRecord(Direction direction, long count, Function<CurrentRecord, R> function, Consumer<ResultStates> consumer);

    Stream<ResultItem> fetchRecord(Direction direction);

    Stream<ResultItem> fetchRecord(Direction direction, long count);

    ResultStates move(Direction direction);

    ResultStates move(Direction direction, long count);


}
