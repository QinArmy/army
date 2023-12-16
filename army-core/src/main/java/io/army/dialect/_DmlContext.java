package io.army.dialect;

import io.army.criteria.SqlField;
import io.army.meta.FieldMeta;
import io.army.stmt.BatchStmt;

import javax.annotation.Nullable;
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
 *
 * @since 0.6.0
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

        void appendSetLeftItem(SqlField dataField);


    }

    interface _DomainUpdateSpec {
        /**
         * <p>
         * supported only by domain update.
         *
         *
         * @throws UnsupportedOperationException throw when non-domain update.
         */
        boolean isExistsChildFiledInSetClause();

    }

    interface _SingleTableContextSpec {

        void appendFieldFromSub(FieldMeta<?> field);

        /**
         * <p>
         * just append column name, no preceding space ,no preceding table alias
         *
         * <p>
         * This method is designed for postgre EXCLUDED in INSERT statement.
         *
         *
         * @see _SqlContext#appendFieldOnly(FieldMeta)
         */
        void appendFieldOnlyFromSub(FieldMeta<?> field);

    }


}
