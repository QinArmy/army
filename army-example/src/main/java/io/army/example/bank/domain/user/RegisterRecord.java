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
import io.army.example.bank.bean.BankCode;
import io.army.example.common.BaseVersionDomain;

import java.time.LocalDateTime;

@Table(name = "r_register_record",
        indexes = {@Index(name = "r_register_record_uni_request_no", fieldList = "requestNo", unique = true)},
        comment = "user register request record")
public class RegisterRecord extends BaseVersionDomain<RegisterRecord> {

    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    @Column
    private Long id;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL
            , comment = "user id ,primary key of u_user table,when register success.")
    private Long userId;

    @Column(notNull = true, defaultValue = "0", comment = "record handle status")
    private RecordStatus status;

    @Column(notNull = true, updateMode = UpdateMode.IMMUTABLE
            , comment = "partner user id,0 representing bank self,@see u_user table")
    private Long partnerId;

    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime), @Param(name = DEPEND, value = "partnerId")})
    @Column(precision = 30, notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "provide to partner request number")
    private String requestNo;

    @Column(notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "deadline,reject handle request after this")
    private LocalDateTime deadline;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "record handle time")
    private LocalDateTime handleTime;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "record completion time")
    private LocalDateTime completionTime;

    @Column(insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "error code")
    private BankCode bankCode;

    @Column(precision = 125, insertable = false, updateMode = UpdateMode.ONLY_NULL, comment = "error message")
    private String failureMessage;


    @Override
    public Long getId() {
        return id;
    }

    public RegisterRecord setId(Long id) {
        this.id = id;
        return this;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public RegisterRecord setRequestNo(String requestNo) {
        this.requestNo = requestNo;
        return this;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public RegisterRecord setStatus(RecordStatus status) {
        this.status = status;
        return this;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public RegisterRecord setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
        return this;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public RegisterRecord setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
        return this;
    }

    public LocalDateTime getHandleTime() {
        return handleTime;
    }

    public RegisterRecord setHandleTime(LocalDateTime handleTime) {
        this.handleTime = handleTime;
        return this;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public RegisterRecord setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
        return this;
    }

    public Long getUserId() {
        return userId;
    }

    public RegisterRecord setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public BankCode getBankCode() {
        return bankCode;
    }

    public RegisterRecord setBankCode(BankCode bankCode) {
        this.bankCode = bankCode;
        return this;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

    public RegisterRecord setFailureMessage(String failureMessage) {
        this.failureMessage = failureMessage;
        return this;
    }


}
