package io.army.dialect;

import io.army.lang.Nullable;
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
interface DmlContext extends StmtContext {

    @Nullable
    DmlContext parentContext();

    default BatchStmt build(List<?> paramList) {
        throw new UnsupportedOperationException();
    }


    interface MultiStmtBatch extends DmlContext {

        /**
         * <p>
         * when multi-statement ,invoke the next element of batch
         * </p>
         *
         * @throws UnsupportedOperationException non-batch and not multi-statement
         */
        void nextElement();

        int currentIndex();

    }

}
