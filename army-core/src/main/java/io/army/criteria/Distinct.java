package io.army.criteria;

public enum Distinct implements SQLModifier {

    ALL,
    DISTINCT,
    DISTINCTROW;


    @Override
    public String keyWord() {
        return name();
    }

}
