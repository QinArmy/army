package com.example.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.qinarmy.army.util.TimeUtils.DATE_TIME_FORMAT;


/**
 * created  on 2018/4/18.
 */
public class BaseCriteria extends PagingForm {


    private static final long serialVersionUID = -7147445919656653018L;

    /**
     * 后台功能 校验组,用于指定 后台用户 id 等
     */
    public interface Mananger {
    }

    /**
     * true 表示需要返回总行数
     */
    private Boolean queryRowCount;

    /**
     * true 表示 时间正序,false 表示时间倒序.
     * 下层默认是按时间倒序
     */
    private Boolean ascOrder;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime startCreateTime;

    @DateTimeFormat(pattern = DATE_TIME_FORMAT)
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    private LocalDateTime endCreateTime;

    /**
     * 分页查询中上一页的最后一行 id
     */
    private Long lastId;

    private BigInteger bigLastId;

    /**
     * 后台用户id, 仅限后台使用
     */
    @Min(0)
    @NotNull(groups = Mananger.class)
    private Long managerUserId;

    /**
     * 后台用户 名称, 仅限后台使用
     */

    @NotEmpty(groups = Mananger.class)
    private String manageUserName;

    public Boolean getQueryRowCount() {
        if (queryRowCount == null) {
            queryRowCount = Boolean.FALSE;
        }
        return queryRowCount;
    }

    public BaseCriteria setQueryRowCount(Boolean queryRowCount) {
        this.queryRowCount = queryRowCount;
        return this;
    }

    public Boolean getAscOrder() {
        if (ascOrder == null) {
            ascOrder = Boolean.FALSE;
        }
        return ascOrder;
    }

    public BaseCriteria setAscOrder(Boolean ascOrder) {
        this.ascOrder = ascOrder;
        return this;
    }

    public LocalDateTime getStartCreateTime() {
        return startCreateTime;
    }

    public BaseCriteria setStartCreateTime(LocalDateTime startCreateTime) {
        this.startCreateTime = startCreateTime;
        return this;
    }

    public LocalDateTime getEndCreateTime() {
        return endCreateTime;
    }

    public BaseCriteria setEndCreateTime(LocalDateTime endCreateTime) {
        this.endCreateTime = endCreateTime;
        return this;
    }

    public Long getLastId() {
        return lastId;
    }

    public BaseCriteria setLastId(Long lastId) {
        this.lastId = lastId;
        return this;
    }

    public BigInteger getBigLastId() {
        return bigLastId;
    }

    public BaseCriteria setBigLastId(BigInteger bigLastId) {
        this.bigLastId = bigLastId;
        return this;
    }

    public Long getManagerUserId() {
        return managerUserId;
    }

    public BaseCriteria setManagerUserId(Long managerUserId) {
        this.managerUserId = managerUserId;
        return this;
    }

    public String getManageUserName() {
        return manageUserName;
    }

    public BaseCriteria setManageUserName(String manageUserName) {
        this.manageUserName = manageUserName;
        return this;
    }
}
