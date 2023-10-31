package io.army.reactive;

import io.army.session.Cursor;


/**
 * <p> This interface representing reactive sql cursor
 *
 * @see ReactiveSession
 */
public interface ReactiveCursor extends Cursor, ReactiveCloseable {

    @Override
    ReactiveSession session();


}
