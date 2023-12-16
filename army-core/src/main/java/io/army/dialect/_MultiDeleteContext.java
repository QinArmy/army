package io.army.dialect;

/**
 * @since 0.6.0
 */
public interface _MultiDeleteContext extends _DeleteContext, _MultiTableStmtContext {

    String parentAlias(String childAlias);

}
