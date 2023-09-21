package io.army.reactive;

import io.army.session.OptionSpec;


/**
 * <p> This interface representing reactive sql cursor
 *
 * @see ReactiveSession
 */
public interface ReactiveCursor extends OptionSpec {

    String name();

    ReactiveSession session();


}
