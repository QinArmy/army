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

package io.army.example.bank.domain.user;

import io.army.annotation.*;
import io.army.example.common.Domain;

import java.time.LocalDateTime;

@Table(name = "captcha"
        , indexes = @Index(name = "uni_request_no", fieldList = "requestNo", unique = true)
        , comment = "safe captcha for user register request")
public class Captcha extends Domain {

    @Generator(type = GeneratorType.POST)
    @Column
    private Long id;

    @Column
    private LocalDateTime createTime;

    @Column
    private LocalDateTime updateTime;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE
            , comment = "partner user id,@see table u_user,0 representing bank self")
    private Long partnerId;

    @Column(precision = 5, nullable = false, comment = "provide to terminate user captcha")
    private String captcha;

    @Column(nullable = false, comment = "deadline,invalid after this")
    private LocalDateTime deadline;

    @Column(precision = 30, nullable = false, updateMode = UpdateMode.IMMUTABLE
            , comment = "request number that provide to partner")
    private String requestNo;


    @Override
    public Long getId() {
        return id;
    }

    public Captcha setId(Long id) {
        this.id = id;
        return this;
    }

    public String getCaptcha() {
        return captcha;
    }

    public Captcha setCaptcha(String captcha) {
        this.captcha = captcha;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public Captcha setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public Captcha setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Captcha setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
        return this;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public Captcha setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public Captcha setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }


}
