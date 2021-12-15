package io.army.criteria;

import io.army.criteria.impl.inner._SelfDescribed;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *          <li>{@link DerivedTable}</li>
 *          <li>{@link io.army.meta.TableMeta}</li>
 *     </ul>
 * </p>
 */
public interface TableAble extends _SelfDescribed {


    /**
     * @return text of tale.
     */
    @Override
    String toString();


}
