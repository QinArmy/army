package io.army.criteria;

/**
 * <p>
 * This interface representing single select item in select list clause.
 * </p>
 *
 * @see io.army.meta.FieldMeta
 * @see QualifiedField
 * @see Expression#as(String)
 * @see io.army.meta.FieldMeta#as(String)
 * @see QualifiedField#as(String)
 * @since 1.0
 */
public interface Selection extends SelectItem, TypeInfer, SortItem {

    String alias();


    static Selection THIS(Selection selection) {
        return selection;
    }


}
