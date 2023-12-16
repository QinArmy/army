package io.army.criteria;

/**
 * <p>
 * This interface representing dml statement .This interface is base interface of below:
 *     <ul>
 *         <li>{@link InsertStatement}</li>
 *         <li>{@link UpdateStatement}</li>
 *         <li>{@link DeleteStatement}</li>
 *         <li>{@link SimpleDmlStatement}</li>
 *         <li>{@link BatchDmlStatement}</li>
 *     </ul>
 *
 * @since 0.6.0
 */
public interface DmlStatement extends PrimaryStatement, Statement.DmlStatementSpec {


}
