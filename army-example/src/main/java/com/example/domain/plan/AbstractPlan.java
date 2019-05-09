package com.example.domain.plan;

import com.example.domain.VersionDomain;
import org.qinarmy.army.annotation.Column;
import org.qinarmy.army.annotation.MappedSuperclass;

/**
 * created  on 2018/11/19.
 */
@MappedSuperclass
public abstract class AbstractPlan extends VersionDomain {

    @Column(defaultValue = ZERO, updatable = false, comment = "id")
    private Long id;

    @Column(defaultValue = ZERO, updatable = false, comment = "用户id")
    private Long userId;


    public Long getId() {
        return id;
    }

    public AbstractPlan setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public AbstractPlan setUserId(Long userId) {
        this.userId = userId;
        return this;
    }
}
