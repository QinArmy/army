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
