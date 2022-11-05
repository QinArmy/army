package io.army.dialect;

/**
 * @since 1.0
 */
public interface _MultiDeleteContext extends _DeleteContext, _MultiTableContext {

    String parentAlias(String childAlias);

}
