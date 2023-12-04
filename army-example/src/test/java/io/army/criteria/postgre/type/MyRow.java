package io.army.criteria.postgre.type;


public final class MyRow {

    private Integer level;

    private String[] textArray;

    private MySubRow subRow;

    public Integer getLevel() {
        return level;
    }

    public MyRow setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public String[] getTextArray() {
        return textArray;
    }

    public MyRow setTextArray(String[] textArray) {
        this.textArray = textArray;
        return this;
    }

    public MySubRow getSubRow() {
        return subRow;
    }

    public MyRow setSubRow(MySubRow subRow) {
        this.subRow = subRow;
        return this;
    }


}
