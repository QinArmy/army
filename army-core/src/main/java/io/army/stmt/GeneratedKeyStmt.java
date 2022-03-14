package io.army.stmt;

import io.army.bean.ObjectWrapper;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface GeneratedKeyStmt extends SimpleStmt {

    String primaryKeyName();

    List<ObjectWrapper> domainList();

    PrimaryFieldMeta<?> idMeta();

}
