package io.army.criteria.impl.inner;

import io.army.criteria.dialect.SubQuery;
import io.army.dialect.DialectParser;

/**
 * <p>
 * This interface representing lateral {@link SubQuery} that can be accessed by {@link DialectParser}.
 * </p>
 * <p>
 * <strong>Note:</strong><br/>
 * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
 * ,because this is army inner api.
 * </p>
 *
 * @since 1.0
 */
@Deprecated
public interface _LateralSubQuery extends _SubQuery {

}
