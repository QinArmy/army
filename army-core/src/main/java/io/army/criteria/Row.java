package io.army.criteria;

import io.army.domain.IDomain;

import java.util.List;

public interface Row<T extends IDomain> extends SetTargetPart {

    List<? extends GenericField<T, ?>> columnList();


}
