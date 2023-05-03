package io.army.criteria.postgre.type;


public final class MySubRow {

    private Integer number;

    private String text;


    public Integer getNumber() {
        return number;
    }

    public MySubRow setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public String getText() {
        return text;
    }

    public MySubRow setText(String text) {
        this.text = text;
        return this;
    }


}
