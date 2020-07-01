package com.example.criteria;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static io.army.util.TimeUtils.DATE_TIME_FORMAT;

public class BaseCriteria {

    private Integer offset;

    private Integer rowCount;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startCreateTime;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endCreateTime;

    private Long lastId;

    private SortOption sortOption;

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

    public final LocalDateTime getStartCreateTime() {
        return startCreateTime;
    }

    public final void setStartCreateTime(LocalDateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
    }

    public final LocalDateTime getEndCreateTime() {
        return endCreateTime;
    }

    public final void setEndCreateTime(LocalDateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public final Long getLastId() {
        return lastId;
    }

    public final void setLastId(Long lastId) {
        this.lastId = lastId;
    }

    public final SortOption getSortOption() {
        return sortOption;
    }

    public final void setSortOption(SortOption sortOption) {
        this.sortOption = sortOption;
    }
}
