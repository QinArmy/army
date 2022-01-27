package io.army.criteria.impl.inner;

import io.army.beans.ObjectWrapper;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _ValuesInsert extends _Insert {

    boolean isMigration();

    Map<FieldMeta<?, ?>, _Expression> commonExpMap();

    List<ObjectWrapper> domainList();

}
