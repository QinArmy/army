package io.army.example.common;

import io.army.annotation.Column;

import java.time.LocalDateTime;

@SuppressWarnings("unchecked")
public abstract class BaseDomain<T extends BaseDomain<T>> extends Domain {

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Boolean visible;


    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public T setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return (T) this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public T setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return (T) this;
    }


    public Boolean getVisible() {
        return visible;
    }

    public T setVisible(Boolean visible) {
        this.visible = visible;
        return (T) this;
    }


}
