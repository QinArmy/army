package io.army.criteria;


/**
 * <p>
 * Throw when don't specified field alias for some column function that no explicit field name. for example
 * Postgre jsonbPathQuery() sql function.
 * </p>
 *
 * @since 1.0
 */
public final class NoColumnFuncFieldAliasException extends CriteriaException {


    public NoColumnFuncFieldAliasException(String message) {
        super(message);
    }


}
