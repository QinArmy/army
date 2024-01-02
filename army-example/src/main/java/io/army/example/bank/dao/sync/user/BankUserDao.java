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

package io.army.example.bank.dao.sync.user;

import io.army.example.bank.domain.user.BankUserType;
import io.army.example.bank.domain.user.Certificate;
import io.army.example.bank.domain.user.CertificateType;
import io.army.example.common.Pair;
import io.army.example.common.SyncBaseDao;
import org.springframework.lang.Nullable;

public interface BankUserDao extends SyncBaseDao {

    boolean isExists(String certificateNo, CertificateType certificateType, BankUserType userType);

    @Nullable
    <P, T extends Certificate<T>> T getCertificate(String certificateNo, CertificateType certificateType
            , Class<T> domainType);

    @Nullable
    Pair<Long, BankUserType> getUserPair(String userNo);

}
