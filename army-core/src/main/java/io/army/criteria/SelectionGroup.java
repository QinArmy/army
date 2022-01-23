package io.army.criteria;


import java.util.List;

public interface SelectionGroup extends SelectPart {

    List<? extends Selection> selectionList();


}
