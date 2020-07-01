package com.example.domain;

import com.example.criteria.BaseCriteria;
import io.army.annotation.Column;
import io.army.annotation.Generator;
import io.army.annotation.MappedSuperclass;
import io.army.domain.IDomain;
import io.army.util.JsonUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
public abstract class Domain extends BaseCriteria implements IDomain {


    @Generator("io.army.generator.snowflake.SnowflakeGenerator")
    @Column
    protected Long id;

    @Column
    protected LocalDateTime createTime;

    @Override
    public int hashCode() {
        return this.id == null ? 0 : this.id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!getClass().isInstance(obj)) {
            return false;
        }
        return Objects.equals(this.id, ((Domain) obj).id);
    }

    @Override
    public String toString() {
        return JsonUtils.writeValue(this);
    }

    @Override
    public Long getId() {
        return id;
    }

    public Domain setId(Long id) {
        this.id = id;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Domain setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

}
