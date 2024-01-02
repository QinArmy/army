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
import io.army.example.common.BaseVersionDomain;

import java.time.LocalDateTime;

@Table(name = "u_user"
        , indexes = {
        @Index(name = "uni_user_no", fieldList = "userNo", unique = true)
        , @Index(name = "inx_certificate_id", fieldList = "certificateId")}
        , comment = "bank user")
@Inheritance("userType")
@SuppressWarnings("unchecked")
public class BankUser<T extends BankUser<T>> extends BaseVersionDomain<T> {


    @Column
    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    private Long id;

    @Column
    private BankUserType userType;

    @Column(nullable = false, precision = 40, updateMode = UpdateMode.IMMUTABLE, comment = "provide to partner user number")
    @Generator(value = SNOWFLAKE, params = {
            @Param(name = START_TIME, value = startTime)
            , @Param(name = DEPEND, value = "partnerUserId")})
    private String userNo;

    @Column(precision = 50, comment = "user nick name")
    private String nickName;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "user certificate table id")
    private Long certificateId;

    @Column(updateMode = UpdateMode.ONLY_NULL, comment = "user register complete time")
    private LocalDateTime completeTime;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "partner user id for person user,0 representing bank self.")
    private Long partnerUserId;

    @Column(nullable = false, updateMode = UpdateMode.IMMUTABLE, comment = "r_register_record primary key")
    private Long registerRecordId;


    public final BankUserType getUserType() {
        return userType;
    }

    public final T setUserType(BankUserType userType) {
        this.userType = userType;
        return (T) this;
    }

    public String getUserNo() {
        return userNo;
    }

    public T setUserNo(String userNo) {
        this.userNo = userNo;
        return (T) this;
    }

    public final Long getCertificateId() {
        return certificateId;
    }

    public final T setCertificateId(Long certificateId) {
        this.certificateId = certificateId;
        return (T) this;
    }


    public final String getNickName() {
        return nickName;
    }

    public final T setNickName(String nickName) {
        this.nickName = nickName;
        return (T) this;
    }

    public final LocalDateTime getCompleteTime() {
        return completeTime;
    }

    public final BankUser<T> setCompleteTime(LocalDateTime completeTime) {
        this.completeTime = completeTime;
        return this;
    }

    public Long getPartnerUserId() {
        return partnerUserId;
    }

    public T setPartnerUserId(Long partnerUserId) {
        this.partnerUserId = partnerUserId;
        return (T) this;
    }

    @Override
    public Long getId() {
        return id;
    }

    public T setId(Long id) {
        this.id = id;
        return (T) this;
    }


    public Long getRegisterRecordId() {
        return registerRecordId;
    }

    public T setRegisterRecordId(Long registerRecordId) {
        this.registerRecordId = registerRecordId;
        return (T) this;
    }


}
