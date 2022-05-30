package io.army.stmt;

import io.army.bean.ObjectAccessor;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.PrimaryFieldMeta;

import java.util.List;

/**
 * <p>
 * This interface representing a insert statement that has auto increment id.
 * </p>
 */
public interface GeneratedKeyStmt extends SimpleStmt {


    ObjectAccessor domainAccessor();

    List<IDomain> domainList();

    @Nullable
    PrimaryFieldMeta<?> idField();

    @Nullable
    String idReturnAlias();

}
