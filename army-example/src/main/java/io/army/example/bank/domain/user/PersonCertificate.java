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
import io.army.example.common.Gender;

import java.time.LocalDate;

@Table(name = "person_certificate", comment = "bank person certificate")
@DiscriminatorValue(CertificateType.Constant.PERSON)
public class PersonCertificate extends Certificate<PersonCertificate> {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "person user birthday")
    private LocalDate birthday;

    @Column(updateMode = UpdateMode.IMMUTABLE, precision = 50, comment = "person user nation")
    private String nation;

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "user gender")
    private Gender gender;

    public LocalDate getBirthday() {
        return birthday;
    }

    public PersonCertificate setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getNation() {
        return nation;
    }

    public PersonCertificate setNation(String nation) {
        this.nation = nation;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public PersonCertificate setGender(Gender gender) {
        this.gender = gender;
        return this;
    }


}
