package com.example.domain;

import io.army.annotation.Column;
import io.army.domain.IDomain;

import java.time.LocalDateTime;

/**
 * created  on 2018/10/21.
 */
public class User implements IDomain {

    private Long id;

    private String name;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;

    @Column
    private Integer version;


    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public User setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public User setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public User setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public User setVersion(Integer version) {
        this.version = version;
        return this;
    }
}
