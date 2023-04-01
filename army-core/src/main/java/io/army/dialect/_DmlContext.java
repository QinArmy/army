package io.army.dialect;

import io.army.criteria.DataField;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.stmt.BatchStmt;

import java.util.List;

/**
 * <p>
 * Package interface,representing dml statement context,this interface is base interface of below:
 *     <ul>
 *         <li>{@link  _InsertContext}</li>
 *         <li>{@link  _SingleUpdateContext}</li>
 *         <li>{@link  _SingleDeleteContext}</li>
 *         <li>{@link  _MultiUpdateContext}</li>
 *         <li>{@link  _MultiDeleteContext}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public  interface _DmlContext extends _PrimaryContext {

    @Nullable
    _DmlContext parentContext();

    @Deprecated
    default BatchStmt build(List<?> paramList) {
        throw new UnsupportedOperationException();
    }


    interface ConditionFieldsSpec {

        void appendConditionFields();
    }


    interface _SetClauseContextSpec {

        void appendSetLeftItem(DataField dataField);


    }

    interface _DomainUpdateSpec {
        /**
         * <p>
         * supported only by domain update.
         * </p>
         *
         * @throws UnsupportedOperationException throw when non-domain update.
         */
        boolean isExistsChildFiledInSetClause();

    }

    interface _SingleTableContextSpec {

        void appendFieldFromSub(FieldMeta<?> field);

    }


}
