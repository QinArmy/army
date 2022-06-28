package io.army.criteria.impl.inner;

import io.army.meta.FieldMeta;

import java.util.List;
import java.util.Map;

public interface _ValueInsert extends _Insert._CommonExpInsert {


    List<Map<FieldMeta<?>, _Expression>> rowValuesList();


}
