package io.army.meta;

/**
 * <p>
 * This interface representing database object ,this interface is base interface of below:
 *     <ul>
 *         <li>{@link  TableMeta}</li>
 *         <li>{@link  io.army.criteria.TableField}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DatabaseObject {

    String objectName();

}
