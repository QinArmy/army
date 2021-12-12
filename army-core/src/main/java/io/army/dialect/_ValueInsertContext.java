package io.army.dialect;

import io.army.beans.ObjectWrapper;
import io.army.criteria.impl.inner._Expression;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _ValueInsertContext extends _DmlContext {

    boolean migration();

    List<FieldMeta<?, ?>> fields();

    List<FieldMeta<?, ?>> parentFields();

    Map<FieldMeta<?, ?>, _Expression<?>> commonExpMap();

    List<ObjectWrapper> domainList();

}
