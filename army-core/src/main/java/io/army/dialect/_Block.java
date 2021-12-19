package io.army.dialect;

import io.army.meta.TableMeta;

/**
 * <p>
 * This class is base interface of below :
 *     <ul>
 *         <li>{@link _SetBlock}</li>
 *     </ul>
 * </p>
 */
public interface _Block extends _SqlContext {

    TableMeta<?> table();

    String tableAlias();

    String safeTableAlias();


}
