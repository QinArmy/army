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

import io.army.annotation.Column;
import io.army.annotation.DiscriminatorValue;
import io.army.annotation.Table;
import io.army.annotation.UpdateMode;

import java.time.LocalDate;

@Table(name = "enterprise_certificate", comment = "bank enterprise certificate")
@DiscriminatorValue(CertificateType.Constant.ENTERPRISE)
public class EnterpriseCertificate extends Certificate<EnterpriseCertificate> {

    @Column(notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "enterprise user register day")
    private LocalDate registerDay;

    @Column(precision = 40, notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "enterprise Unified social credit code")
    private String creditCode;

    @Column(notNull = true, defaultValue = "0", comment = "enterprise legal person name")
    private Long legalPersonCertificateId;


    public LocalDate getRegisterDay() {
        return registerDay;
    }

    public EnterpriseCertificate setRegisterDay(LocalDate registerDay) {
        this.registerDay = registerDay;
        return this;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public EnterpriseCertificate setCreditCode(String creditCode) {
        this.creditCode = creditCode;
        return this;
    }

    public Long getLegalPersonCertificateId() {
        return legalPersonCertificateId;
    }

    public EnterpriseCertificate setLegalPersonCertificateId(Long legalPersonCertificateId) {
        this.legalPersonCertificateId = legalPersonCertificateId;
        return this;
    }


}
