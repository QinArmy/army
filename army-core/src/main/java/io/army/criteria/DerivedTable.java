package io.army.criteria;

import io.army.lang.Nullable;

import java.util.List;

public interface DerivedTable extends TableItem {

    List<? extends SelectItem> selectPartList();

    @Nullable
    default Selection selection(String derivedFieldName) {
        throw new UnsupportedOperationException();
    }

}
