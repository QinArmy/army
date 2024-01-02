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

package io.army.criteria.standard.unit;

import io.army.criteria.PrimaryStatement;
import io.army.criteria.Visible;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.util._Collections;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.List;

abstract class StandardUnitTests {


    final List<ChinaProvince> createProvinceList() {
        List<ChinaProvince> domainList = _Collections.arrayList();
        ChinaProvince p;
        for (int i = 0; i < 2; i++) {
            p = new ChinaProvince();
            p.setId((long) i);
            p.setName("江湖" + i);
            p.setGovernor("盟主");
            p.setRegionGdp(new BigDecimal("8888.88"));
            p.setProvincialCapital("总堂");
            domainList.add(p);
        }
        return domainList;
    }


    static void printStmt(final Logger logger, final PrimaryStatement statement) {
        for (Database database : Database.values()) {
            switch (database) {
                case MySQL:
                case PostgreSQL:
                    break;
                default:
                    continue;
            }
            for (Dialect dialect : database.dialects()) {
                logger.debug("{}:\n{}", dialect.name(), statement.mockAsString(dialect, Visible.ONLY_VISIBLE, true));
            }
        }


    }


}
