package io.army.sync;

import io.army.session.*;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @see ResultStates#valueOf(Option)
 * @see SyncSession#SYNC_CURSOR
 * @since 1.0
 */
public interface SyncCursor extends NamedCursor, AutoCloseable {


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
