package io.army.domain;

import org.qinarmy.foundation.criteria.BaseCriteria;
import org.qinarmy.foundation.orm.IVersionDomain;
import org.qinarmy.foundation.util.JsonUtils;
import org.springframework.lang.NonNull;

import java.time.LocalDateTime;

public abstract class ArmyDomain extends BaseCriteria implements IDomain, IVersionDomain {

    private Long id;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer version;

    private Boolean visible;

    @Override
    public boolean equals(Object o) {
        return IVersionDomain.versionDomainEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IVersionDomain.versionDomainHash(this);
    }

    @NonNull
    @Override
    public String toString() {
        return JsonUtils.writeValue(this);
    }

    @Override
    public Long getId() {
        return id;
    }

    public ArmyDomain setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public ArmyDomain setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public ArmyDomain setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public ArmyDomain setVersion(Integer version) {
        this.version = version;
        return this;
    }

    @Override
    public Boolean getVisible() {
        return visible;
    }

    public ArmyDomain setVisible(Boolean visible) {
        this.visible = visible;
        return this;
    }
}
