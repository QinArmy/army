package io.army.stmt;

import io.army.bean.ObjectWrapper;
import io.army.criteria.Selection;
import io.army.meta.FieldMeta;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface InsertStmt extends Stmt {

    /**
     * @return {@link java.util.Collections#singletonList(Object)} or {@link Collections#emptyList()}
     */
    List<Selection> selectionList();

    List<ObjectWrapper> domainList();

    FieldMeta<?, ?> idMeta();

}
