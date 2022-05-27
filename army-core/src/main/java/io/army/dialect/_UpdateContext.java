package io.army.dialect;


import io.army.criteria.DataField;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _DomainUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
public interface _UpdateContext extends _SqlContext {

    void appendSetLeftItem(DataField field);


}
