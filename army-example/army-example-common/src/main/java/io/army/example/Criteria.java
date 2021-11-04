package io.army.example;

public class Criteria {

    private Integer offset;

    private Integer rowCount;

    private Long lastId;

    public final Integer getOffset() {
        return offset;
    }

    public final void setOffset(Integer offset) {
        this.offset = offset;
    }

    public final Integer getRowCount() {
        return rowCount;
    }

    public final void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public final Long getLastId() {
        return lastId;
    }

    public final void setLastId(Long lastId) {
        this.lastId = lastId;
    }


}
