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
public interface DmlStatement extends Statement {


    /**
     * This is base interface of below:
     * <ul>
     *     <li>{@link Insert}</li>
     *     <li>{@link SubInsert}</li>
     * </ul>
     *
     * @since 1.0
     */
    interface DmlInsert {

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


}
