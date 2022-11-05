package io.army.dialect;


/**
 * <p>
 * Packet interface,this interface is base interface of below:
 *     <ul>
 *         <li>{@link _UpdateContext}</li>
 *         <li>{@link _DeleteContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
interface NarrowDmlContext extends DmlContext {

   /**
    * <p>
    * when multi-statement ,invoke the next element of batch
    * </p>
    *
    * @throws UnsupportedOperationException non-batch and not multi-statement
    */
   void nextElement();

   int currentIndex();

   boolean hasNamedValue();

   boolean isBatchEnd();

}
