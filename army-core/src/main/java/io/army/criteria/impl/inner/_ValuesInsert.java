package io.army.criteria.impl.inner;

import io.army.bean.ObjectWrapper;
import io.army.criteria.NullHandleMode;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _ValuesInsert extends _Insert {

    boolean isMigration();

    NullHandleMode nullHandle();

    Map<FieldMeta<?>, _Expression> commonExpMap();

    List<ObjectWrapper> domainList();

}
