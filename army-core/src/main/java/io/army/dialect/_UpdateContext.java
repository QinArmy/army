package io.army.dialect;


import io.army.stmt.SimpleStmt;

/**
 * <p>
 * This interface is base interface of below:
 *     <ul>
 *         <li>{@link _SingleUpdateContext}</li>
 *         <li>{@link _MultiUpdateContext}</li>
 *     </ul>
 * </p>
 */
public interface _UpdateContext extends _DmlContext {


    /**
     * @return true : SET clause support table alias
     */
    @Deprecated
    boolean setClauseTableAlias();

    @Override
    SimpleStmt build();

}
