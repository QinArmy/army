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

package io.army.example.bank.dao.sync;

import io.army.criteria.Select;
import io.army.example.common.ArmySyncBaseDao;
import io.army.example.common.BaseService;
import io.army.example.common.Pair;
import io.army.sync.SyncSession;
import io.army.sync.SyncSessionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository("bankSyncBaseDao")
@Profile(BaseService.SYNC)
public class BankSyncBaseDao extends ArmySyncBaseDao {

    @Autowired
    public void setSessionContext(@Qualifier("bankSyncSessionContext") SyncSessionContext sessionContext) {
        this.sessionContext = sessionContext;
    }

    @SuppressWarnings("unchecked")
    protected final <F, S> Pair<F, S> selectAsPair(SyncSession session, Select stmt) {
        return (Pair<F, S>) session.queryOne(stmt, Pair.class);
    }


}
