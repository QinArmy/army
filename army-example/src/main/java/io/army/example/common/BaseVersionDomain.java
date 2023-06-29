package io.army.example.common;

import io.army.annotation.Column;
import io.army.annotation.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class BaseVersionDomain<T extends BaseVersionDomain<T>> extends VersionDomain {

    @Column(scale = 2)
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;


    public final LocalDateTime getCreateTime() {
        return createTime;
    }

    public final T setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return (T) this;
    }

    public final LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public final T setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return (T) this;
    }

    @Override
    public final Integer getVersion() {
        return version;
    }

    public final T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }

    public final Boolean getVisible() {
        return visible;
    }

    public final T setVisible(Boolean visible) {
        this.visible = visible;
        return (T) this;
    }


}
