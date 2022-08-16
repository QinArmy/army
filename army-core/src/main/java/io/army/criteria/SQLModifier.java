package io.army.criteria;

public enum SQLModifier implements SQLWords {

    ALL(" ALL"),
    DISTINCT(" DISTINCT"),
    DISTINCTROW(" DISTINCTROW");

    public final String keyWords;

    SQLModifier(String keyWords) {
        this.keyWords = keyWords;
    }

    @Override
    public final String render() {
        return this.keyWords;
    }


    @Override
    public final String toString() {
        return String.format("%s.%s", SQLModifier.class.getName(), this.name());
    }


}
