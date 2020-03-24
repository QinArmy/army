package io.army.criteria;

public enum Distinct implements SQLModifier {

    ALL,
    DISTINCT,
    DISTINCTROW;


    @Override
    public String render() {
        return name();
    }

}
