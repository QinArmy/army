package io.army.criteria.impl.inner;

import io.army.criteria.TableItem;

/**
 * <p>
 * This interface representing {@link _TableBlock} without {@link io.army.criteria.TableItem}.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link io.army.criteria.impl._JoinType}</li>
 *     <li>{@link _LeftBracketBlock}</li>
 *     <li>{@link _RightBracketBlock}</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _NoTableBlock extends _TableBlock {

    /**
     * @throws UnsupportedOperationException always
     */
    @Override
    TableItem tableItem();

}
