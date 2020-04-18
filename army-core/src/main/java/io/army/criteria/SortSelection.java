package io.army.criteria;

public interface SortSelection extends Selection, SortPart {

    SortPart asc();

    SortPart desc();

}
