package com.example.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 用于接收客户端参数(带分页参数) 的基类
 * created  on 02/03/2018.
 */
public class PagingForm extends Form {

    private static final long serialVersionUID = 6918162099117025846L;

    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    @Min(0)
    private Integer offset;


    /**
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    @Min(1)
    @Max(1000)
    private Integer rowCount;

    /**
     * 兼容 easy ui
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    public Integer getOffset() {
        if (offset == null) {
            offset = 0;
        }
        return offset;
    }

    public PagingForm setOffset(Integer offset) {
        this.offset = offset;
        return this;
    }

    /**
     * 兼容 easy ui
     * 在 getter 中设置了默认值,否则会在 dao 中查出错
     */
    public Integer getRowCount() {
        if (rowCount == null) {
            rowCount = 10;
        }
        return rowCount;
    }

    public PagingForm setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
        return this;
    }
}
