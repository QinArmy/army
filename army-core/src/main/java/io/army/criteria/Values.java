package io.army.criteria;

/**
 * <p>
 * This interface representing VALUES statement
 * </p>
 *
 * @since 1.0
 */
public interface Values extends DqlStatement, DialectStatement, RowSet.DqlValues {

    interface _ValuesSpec<U extends DqlValues> {
        U asValues();
    }


}
