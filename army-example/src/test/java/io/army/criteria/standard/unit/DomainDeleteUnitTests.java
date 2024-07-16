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

import io.army.criteria.DeleteStatement;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Map;

import static io.army.criteria.impl.SQLs.AND;
import static io.army.criteria.impl.SQLs.AS;


public class DomainDeleteUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(DomainDeleteUnitTests.class);


    @Test
    public void deleteParent() {
        final Map<String, Object> map = _Collections.hashMap();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final DeleteStatement stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.id::between, SQLs::literal, map.get("firstId"), AND, map.get("secondId"))
                .and(ChinaRegion_.name.equal(SQLs::literal, "江湖"))
                .asDelete();
        printStmt(LOG, stmt);
    }

    @Test
    public void deleteChild() {
        final DeleteStatement stmt;
        stmt = SQLs.domainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.equal(SQLs::literal, 1))
                .and(ChinaRegion_.name::equal, SQLs::param, "江湖")
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .asDelete();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchDeleteParent() {
        final DeleteStatement stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaRegion_.T, AS, "cr")
                .where(ChinaRegion_.id::spaceEqual, SQLs::namedParam)
                .and(ChinaRegion_.version::equal, SQLs::param, "0")
                .asDelete()
                .namedParamList(this.createProvinceList());

        printStmt(LOG, stmt);

    }

    @Test
    public void batchDeleteChild() {

        final DeleteStatement stmt;
        stmt = SQLs.batchDomainDelete()
                .deleteFrom(ChinaProvince_.T, AS, "p")
                .where(ChinaProvince_.id.equal(SQLs::namedParam, ChinaRegion_.ID))
                .and(ChinaRegion_.name.equal(SQLs::namedParam, ChinaRegion_.NAME))
                .and(ChinaProvince_.governor.equal(SQLs::param, "石教主").or(consumer -> {
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "钟教主"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "老钟"));
                            consumer.accept(ChinaProvince_.governor.equal(SQLs::param, "方腊"));
                        })
                )
                .asDelete()
                .namedParamList(this.createProvinceList());

        printStmt(LOG, stmt);

    }


}
