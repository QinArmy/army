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

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

import static io.army.criteria.impl.SQLs.AS;


@Repository("bankSyncStandardAccountDao")
@Profile({BaseService.SYNC, BeanUtils.STANDARD})
public class StandardAccountDao extends BankSyncBaseDao implements BankAccountDao {


    @Override
    public Map<String, Object> getRegisterUserInfo(final String requestNo) {
        final Select stmt;
        stmt = SQLs.query()
                .select(s -> s.space(SQLs.field("u", BankUser_.userNo))
                        .comma(SQLs.field("u", BankUser_.userType))
                        .comma(BankAccount_.accountNo)
                        .comma(BankAccount_.accountType)
                        .comma(SQLs.field("pu", BankUser_.userNo))
                        .comma(RegisterRecord_.createTime)
                        .comma(RegisterRecord_.handleTime)
                        .comma(RegisterRecord_.completionTime)
                )
                .from(RegisterRecord_.T, AS, "r")
                .join(BankUser_.T, AS, "pu").on(RegisterRecord_.partnerId.equal(SQLs.field("pu", BankUser_.id)))
                .join(BankUser_.T, AS, "u").on(RegisterRecord_.userId.equal(SQLs.field("u", BankUser_.id)))
                .join(BankAccount_.T, AS, "a").on(BankAccount_.userId.equal(SQLs.field("u", BankUser_.id)))
                .where(RegisterRecord_.requestNo::equal, SQLs::param, requestNo)
                .and(RegisterRecord_.id.equal(SQLs.field("u", BankUser_.registerRecordId)))
                .and(RegisterRecord_.id.equal(BankAccount_.registerRecordId))
                .asQuery();
        return this.sessionContext.currentSession().queryOneObject(stmt, HashMap::new);
    }


    @Override
    public Map<String, Object> getPartnerAccountStatus(String requestNo, String certificateNo
            , CertificateType certificateType) {

        final Select stmt;
        stmt = SQLs.query()
                .select(RegisterRecord_.requestNo, BankUser_.userNo, BankUser_.userType, BankAccount_.accountNo)
                .comma(BankAccount_.accountType)
                .from(RegisterRecord_.T, AS, "r")
                .join(BankUser_.T, AS, "u").on(BankUser_.id::equal, RegisterRecord_.userId)
                .join(BankAccount_.T, AS, "a").on(BankUser_.id::equal, BankAccount_.userId)
                .join(Certificate_.T, AS, "c").on(Certificate_.id::equal, BankUser_.certificateId)
                .where(RegisterRecord_.requestNo.equal(SQLs::param, requestNo))
                .and(Certificate_.certificateNo.equal(SQLs::literal, certificateNo))
                .and(Certificate_.certificateType::equal, SQLs::literal, certificateType)
                .and(BankUser_.userType::equal, SQLs::literal, BankUserType.PARTNER)
                .and(BankUser_.registerRecordId::equal, RegisterRecord_.id)
                .and(BankAccount_.registerRecordId::equal, RegisterRecord_.id)
                .asQuery();
        return this.sessionContext.currentSession().queryOneObject(stmt, HashMap::new);
    }


}
