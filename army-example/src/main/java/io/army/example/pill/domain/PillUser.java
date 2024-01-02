/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.example.pill.domain;

import io.army.annotation.*;
import io.army.example.common.VersionDomain;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.PillUserType;

import java.time.LocalDateTime;

@Table(name = "u_user", comment = "user", indexes = @Index(name = "idx_identity_id", fieldList = {"identityId"}, unique = true))
@Inheritance("userType")
@SuppressWarnings("unchecked")
public class PillUser<T extends PillUser<T>> extends VersionDomain {

    @Column
    private Long id;

    @Column
    private PillUserType userType;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column
    private Integer version;

    @Column
    private Boolean visible;

    @Column(precision = 64, defaultValue = "''", comment = "user nick name")
    private String nickName;

    @Column(comment = "user identity type", updateMode = UpdateMode.ONLY_NULL)
    private IdentityType identityType;

    @Column(defaultValue = "0", comment = "user identity id", updateMode = UpdateMode.ONLY_NULL)
    private Long identityId;


    @Override
    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }

    public PillUserType getUserType() {
        return userType;
    }

    public T setUserType(PillUserType userType) {
        this.userType = userType;
        return (T) this;
    }

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

    @Override
    public Integer getVersion() {
        return version;
    }

    public T setVersion(Integer version) {
        this.version = version;
        return (T) this;
    }

    public Boolean getVisible() {
        return visible;
    }

    public T setVisible(Boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public String getNickName() {
        return nickName;
    }

    public T setNickName(String nickName) {
        this.nickName = nickName;
        return (T) this;
    }

    public IdentityType getIdentityType() {
        return identityType;
    }

    public T setIdentityType(IdentityType identityType) {
        this.identityType = identityType;
        return (T) this;
    }

    public Long getIdentityId() {
        return identityId;
    }

    public T setIdentityId(Long identityId) {
        this.identityId = identityId;
        return (T) this;
    }


}
