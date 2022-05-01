package io.army.criteria;

public enum Distinct implements SQLWords.Modifier {

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


    @Override
    public final int level() {
        //all is 0
        return 0;
    }

    @Override
    public final String toString() {
        return String.format("%s.%s", Distinct.class.getName(), this.name());
    }


}
