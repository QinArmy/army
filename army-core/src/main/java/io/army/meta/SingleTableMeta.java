package io.army.meta;

import io.army.domain.IDomain;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link SimpleTableMeta}</li>
 *         <li>{@link ParentTableMeta}</li>
 *     </ul>
 * </p>
 *
 * @param <T> domain java type
 */
public interface SingleTableMeta<T extends IDomain> extends TableMeta<T> {

}
