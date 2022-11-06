package io.army.dialect;

import io.army.criteria.QualifiedField;
import io.army.criteria.TabularItem;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

/**
 * <p>
 * This interface representing multi-table context,this interface is base interface of below:
 *     <ul>
 *         <li>{@link  _SimpleQueryContext}</li>
 *         <li>{@link  _MultiUpdateContext}</li>
 *         <li>{@link  _MultiDeleteContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface _MultiTableContext{

        /**
         * <p>
         * This method is designed for the implementation of {@link QualifiedField}
         * </p>
         * <p>
         *     <ol>
         *         <li>append one space</li>
         *         <li>append table alias and point</li>
         *         <li>append safe column name</li>
         *     </ol>
         * </p>
         */
        void appendField(String tableAlias, FieldMeta<?> field);

        /**
         * <p>
         * This method is designed for the implementation of {@link FieldMeta}
         * </p>
         * <p> steps:
         *     <ol>
         *         <li>append one space</li>
         *         <li>append table alias and point if need</li>
         *         <li>append safe column name</li>
         *     </ol>
         * </p>
         */
        void appendField(FieldMeta<?> field);

        String safeTableAlias(TableMeta<?> table, String alias);

        String safeTableAlias(String alias);

        String saTableAliasOf(TableMeta<?> table);

        TabularItem tableItemOf(String tableAlias);


}
