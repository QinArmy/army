package io.army.meta;

import io.army.criteria.SQLElement;

/**
 * <p>
 * This interface representing database object ,this interface is base interface of below:
 *     <ul>
 *         <li>{@link  TableMeta}</li>
 *         <li>{@link  io.army.criteria.TableField}</li>
 *     </ul>
*
 * @since 1.0
 */
public interface DatabaseObject extends SQLElement {

    /**
     * @see TableMeta#tableName()
     * @see FieldMeta#columnName()
     */
    String objectName();

    String comment();

}
