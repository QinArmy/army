package io.army.criteria;

/**
 * <p>
 * This interface representing derived table. This interface possibly is below:
 * <ul>
 *     <li>{@link SubQuery}</li>
 *     <li>{@link SubValues}</li>
 *     <li>the sql function that produce result set,for example JSON_TABLE()</li>
 *     <li>the sub INSERT/UPDATE/DELETE with RETURNING</li>
 * </ul>
 * </p>
 *
 * @since 1.0
 */
public interface DerivedTable extends TabularItem {


}
