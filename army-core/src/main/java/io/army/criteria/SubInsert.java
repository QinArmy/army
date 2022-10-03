package io.army.criteria;

/**
 * <p>
 * This interface representing sub insert that can present in with clause.
 * </p>
 *
 * @since 1.0
 */
public interface SubInsert extends DmlInsert, SubStatement {

    @Deprecated
    interface _SubInsertSpec extends _DmlInsertSpec<SubInsert> {

    }


}
