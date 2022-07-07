package io.army.criteria.impl.inner;

import io.army.criteria.SubQuery;
import io.army.dialect._DialectParser;

/**
 * <p>
 * This interface representing lateral {@link SubQuery} that can be accessed by {@link _DialectParser}.
 * </p>
 * <p>
 * <strong>Note:</strong><br/>
 * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
 * ,because this is army inner api.
 * </p>
 *
 * @since 1.0
 */
public interface _LateralSubQuery extends _SubQuery {

}
