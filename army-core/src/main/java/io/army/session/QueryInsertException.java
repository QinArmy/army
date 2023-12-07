package io.army.session;


/**
 * <p>
 * Throw when {@link Session#isQueryInsertAllowed()} is false and try execute query insert.
 * </p>
 *
 * @see io.army.env.ArmyKey#QUERY_INSERT_MODE
 */
public class QueryInsertException extends SessionException {

    public QueryInsertException(String message) {
        super(message);
    }
}
