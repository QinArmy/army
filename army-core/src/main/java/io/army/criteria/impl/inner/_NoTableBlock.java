package io.army.criteria.impl.inner;

/**
 * <p>
 * This interface representing {@link _TableBlock} without {@link io.army.criteria.TableItem}.
 * This interface is base interface of below:
 * <ul>
 *     <li>{@link io.army.criteria.impl._JoinType}</li>
 *     <li>{@link _LeftBracketBlock}</li>
 *     <li>{@link _RightBracketBlock}</li>
 * </ul>
 *  Any method of {@link _NoTableBlock} always throw {@link UnsupportedOperationException}
 * </p>
 *
 * @since 1.0
 */
public interface _NoTableBlock extends _TableBlock {


}
