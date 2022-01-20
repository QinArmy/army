package io.army.criteria;

public enum Distinct implements SQLModifier {

    ALL(" ALL"),
    DISTINCT(" DISTINCT"),
    DISTINCTROW(" DISTINCTROW");

    public final String keyWords;

    Distinct(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public final String render() {
        return this.keyWords;
    }


}
