package io.army.criteria;

import java.util.List;

public interface DerivedTable extends TablePart {

    List<? extends SelectPart> selectPartList();

    default Selection selection(String derivedFieldName) {
        throw new UnsupportedOperationException();
    }

}
