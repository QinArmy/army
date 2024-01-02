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

package io.army.example.bank.web.form;

import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.CertificateType;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public final class PersonRegisterForm extends RegisterForm {

    @NotNull
    private String partnerUserNo;

    @NotEmpty
    private String name;

    @NotNull
    private CertificateType certificateType;

    @NotEmpty
    private String certificateNo;

    @NotEmpty
    private String phone;

    @NotNull
    private BankAccountType accountType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CertificateType getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public void setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getPartnerUserNo() {
        return partnerUserNo;
    }

    public void setPartnerUserNo(String partnerUserNo) {
        this.partnerUserNo = partnerUserNo;
    }

    public BankAccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(BankAccountType accountType) {
        this.accountType = accountType;
    }
}
