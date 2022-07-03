package io.army.criteria;

/**
 * <p>
 * This interface representing VALUES statement
 * </p>
 *
 * @since 1.0
 */
public interface Values extends RowSet, DerivedTable, DqlStatement {

    interface _ValuesSpec {
        Values asValues();
    }


}
