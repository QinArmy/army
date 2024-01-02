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

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class EnterpriseRegisterForm extends RegisterForm {

    @NotEmpty
    private String name;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String certificateNo;

    @NotEmpty
    private String creditCode;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate registerDay;

    @NotEmpty
    private String city;

    @NotEmpty
    private String legalPerson;

    @NotEmpty
    private String legalPersonCertificateNo;


    public String getName() {
        return name;
    }

    public EnterpriseRegisterForm setName(String name) {
        this.name = name;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public EnterpriseRegisterForm setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getCertificateNo() {
        return certificateNo;
    }

    public EnterpriseRegisterForm setCertificateNo(String certificateNo) {
        this.certificateNo = certificateNo;
        return this;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public EnterpriseRegisterForm setCreditCode(String creditCode) {
        this.creditCode = creditCode;
        return this;
    }

    public LocalDate getRegisterDay() {
        return registerDay;
    }

    public EnterpriseRegisterForm setRegisterDay(LocalDate registerDay) {
        this.registerDay = registerDay;
        return this;
    }

    public String getCity() {
        return city;
    }

    public EnterpriseRegisterForm setCity(String city) {
        this.city = city;
        return this;
    }

    public String getLegalPerson() {
        return legalPerson;
    }

    public EnterpriseRegisterForm setLegalPerson(String legalPerson) {
        this.legalPerson = legalPerson;
        return this;
    }

    public String getLegalPersonCertificateNo() {
        return legalPersonCertificateNo;
    }

    public EnterpriseRegisterForm setLegalPersonCertificateNo(String legalPersonCertificateNo) {
        this.legalPersonCertificateNo = legalPersonCertificateNo;
        return this;
    }
}
