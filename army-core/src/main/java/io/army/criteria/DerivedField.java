package io.army.criteria;

public interface DerivedField extends Expression, Selection {

    String subQueryAlias();

}
