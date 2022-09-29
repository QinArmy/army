package io.army.criteria;

/**
 * <p>
 * This interface representing sub insert that can present in with clause.
 * </p>
 *
 * @since 1.0
 */
public interface SubInsert extends DmlStatement.DmlInsert, SubStatement {

    interface _SubInsertSpec extends DmlStatement._DmlInsertSpec<SubInsert> {

    }


}
