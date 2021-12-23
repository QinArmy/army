package io.army.criteria;

import java.util.List;

public interface DerivedTable extends TablePart {

    List<? extends SelectPart> selectPartList();

    Selection selection(String derivedFieldName);

}
