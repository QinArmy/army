package io.army.dialect;

import io.army.criteria.DataField;
import io.army.meta.SingleTableMeta;

public interface _MultiUpdateContext extends UpdateContext, _MultiTableContext, _SetClauseContext {


    /**
     * <p>
     *     <ul>
     *         <li>dataField table is {@link  SingleTableMeta},return table alias</li>
     *         <li>dataField table is {@link  io.army.meta.ChildTableMeta} return {@link  io.army.meta.ParentTableMeta} alias</li>
     *     </ul>
     * </p>
     */
    String singleTableAliasOf(DataField dataField);


}
