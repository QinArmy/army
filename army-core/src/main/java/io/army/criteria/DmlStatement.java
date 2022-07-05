package io.army.criteria;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link Insert}</li>
 *         <li>{@link Update}</li>
 *         <li>{@link Delete}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DmlStatement extends PrimaryStatement {


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link Insert}</li>
     *     <li>{@link SubInsert}</li>
     *     <li>{@link ReplaceInsert}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlInsert extends Statement {


    }

    interface _DmlInsertSpec<I extends DmlInsert> {

        I asInsert();

    }

    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link Update}</li>
     *     <li>{@link SubUpdate}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlUpdate {


    }

    interface _DmlUpdateSpec<U extends DmlUpdate> {

        U asUpdate();
    }

    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link Delete}</li>
     *     <li>{@link SubDelete}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlDelete {


    }


    interface _DmlDeleteSpec<D extends DmlDelete> {

        D asDelete();
    }


}
