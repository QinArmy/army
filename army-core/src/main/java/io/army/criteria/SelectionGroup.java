package io.army.criteria;


import java.util.List;

public interface SelectionGroup extends SelectPart {

    String tableAlias();

    List<? extends Selection> selectionList();


}
