package io.army.criteria;

/**
 * <p>
 * This interface representing DQL statement,this interface is base interface of below:
 * <ul>
 *     <li>{@link Select}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DqlStatement extends PrimaryStatement {

    interface DqlInsert {

    }

    interface DqlInsertSpec<I extends DqlInsert> {

        I asReturningInsert();
    }

}
