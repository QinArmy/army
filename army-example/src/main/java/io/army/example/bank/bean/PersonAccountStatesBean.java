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

package io.army.example.bank.bean;

import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.BankUserType;
import io.army.example.bank.domain.user.CertificateType;

public class PersonAccountStatesBean implements BankFieldAccessBean {

    public Long partnerUserId;

    public String partnerNo;

    public BankUserType partnerUserType;

    public Long certificateId;

    public CertificateType certificateType;

    public String certificateNo;

    public Long userId;

    public String userNo;

    public BankUserType userType;

    public Long accountId;

    public String accountNo;

    public BankAccountType accountType;


}
