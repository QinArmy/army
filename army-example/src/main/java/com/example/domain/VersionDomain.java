package com.example.domain;

import io.army.annotation.Column;
import io.army.annotation.MappedSuperclass;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class VersionDomain extends Domain {


    @Column
    protected LocalDateTime updateTime;

    @Column
    protected Integer version;


    @Override
    public int hashCode() {
        return this.id == null ? 0 : Objects.hash(this.id, this.version);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && Objects.equals(this.version, ((VersionDomain) obj).version);
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
