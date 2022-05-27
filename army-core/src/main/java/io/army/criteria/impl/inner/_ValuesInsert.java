package io.army.criteria.impl.inner;

import io.army.criteria.NullHandleMode;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _ValuesInsert extends _Insert {

    boolean isMigration();

    NullHandleMode nullHandle();

    boolean isPreferLiteral();

    Map<FieldMeta<?>, _Expression> commonExpMap();

    List<IDomain> domainList();

}
