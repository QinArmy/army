package com.example.error.inheritance.multi;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.domain.IDomain;

import java.time.LocalDateTime;

@Table(name = "parent_one", comment = "first parentMeta")
@Inheritance("type")
public class ParentOne implements IDomain {

    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;

    @Column(comment = "type")
    private MultiParentType type;

    public Long getId() {
        return id;
    }

    public ParentOne setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public ParentOne setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public ParentOne setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public ParentOne setVersion(Integer version) {
        this.version = version;
        return this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public ParentOne setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }

    public MultiParentType getType() {
        return type;
    }

    public ParentOne setType(MultiParentType type) {
        this.type = type;
        return this;
    }
}
