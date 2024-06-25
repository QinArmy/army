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

@Table(name = "u_person", comment = "bank person user")
@DiscriminatorValue(BankUserType.Constant.PERSON)
public class BankPerson extends BankUser<BankPerson> {

    @Column(updateMode = UpdateMode.IMMUTABLE, comment = "the user type that user form.")
    private BankUserType fromPartnerType;

    @Column(notNull = true, updateMode = UpdateMode.IMMUTABLE, comment = "user birthday")
    private LocalDate birthday;

    @Column(notNull = true, precision = 20, comment = "user phone number")
    private String phone;


    public LocalDate getBirthday() {
        return birthday;
    }

    public BankPerson setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public BankPerson setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public BankUserType getFromPartnerType() {
        return fromPartnerType;
    }

    public BankPerson setFromPartnerType(BankUserType fromPartnerType) {
        this.fromPartnerType = fromPartnerType;
        return this;
    }


}
