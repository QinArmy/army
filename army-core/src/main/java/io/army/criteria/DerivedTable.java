package io.army.criteria;

import java.util.List;

public interface DerivedTable extends TableAble {

    List<SelectPart> selectPartList();

    Selection selection(String derivedFieldName);
}
