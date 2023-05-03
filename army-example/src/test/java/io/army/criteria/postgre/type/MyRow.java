package io.army.criteria.postgre.type;


import java.util.List;

public final class MyRow {

    private Integer level;

    private List<String> textList;

    private MySubRow subRow;

    public Integer getLevel() {
        return level;
    }

    public MyRow setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public List<String> getTextList() {
        return textList;
    }

    public MyRow setTextList(List<String> textList) {
        this.textList = textList;
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
