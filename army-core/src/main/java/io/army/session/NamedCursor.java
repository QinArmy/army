package io.army.session;

import io.army.criteria.Selection;

import java.util.List;

public interface NamedCursor extends CloseableSpec, OptionSpec {

    String name();

    List<Selection> selectionList();

    Selection selection(int indexBasedZero);

    Selection selection(String name);

}
