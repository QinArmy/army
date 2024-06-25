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

@Table(name = "uer_certificate"
        , indexes = {@Index(name = "uni_certificate_no_type", fieldList = {"certificateNo", "certificateType"}, unique = true)}
        , comment = "bank user certificate")
@Inheritance("certificateType")
@SuppressWarnings("unchecked")
public class Certificate<T extends Certificate<T>> extends BaseVersionDomain<T> {


    public static Certificate<?> create() {
        return new Certificate<>();
    }

    @SuppressWarnings("unchecked")
    public static final Class<Certificate<?>> CLASS = (Class<Certificate<?>>) ((Class<?>) Certificate.class);


    @Column
    @Generator(value = SNOWFLAKE, params = {@Param(name = START_TIME, value = startTime)})
    private Long id;


    @Column(notNull = false)
    private CertificateType certificateType;

    @Column(precision = 30, notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "user certificate number.")
    private String certificateNo;

    @Column(precision = 50, notNull = true, comment = "person or enterprise name")
    private String subjectName;


    public CertificateType getCertificateType() {
        return certificateType;
    }

    public T setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
        return (T) this;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public T setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
        return (T) this;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public T setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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


}
