package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.domain.IDomain;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface GeneratedKeyStmt extends SimpleStmt {

    String primaryKeyName();

    ObjectAccessor domainAccessor();

    List<IDomain> domainList();

    PrimaryFieldMeta<?> idMeta();

}
