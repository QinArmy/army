package io.army.dialect;

import io.army.criteria.SetTargetPart;
import io.army.criteria.SetValuePart;
import io.army.meta.SingleTableMeta;

import java.util.List;


public interface _SingleUpdateContext extends _DmlContext {

    @Override
    SingleTableMeta<?> tableMeta();

    List<? extends SetTargetPart> targetPartList();

    List<? extends SetValuePart> valuePartList();


}
