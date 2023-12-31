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

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.army.criteria.impl.SQLs.AS;

public class StandardInsertUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardInsertUnitTests.class);


    @Test//(invocationCount = 100)
    public void domainInsertParent() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = SQLs.singleInsert()
//                .migration(true)
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(this::createRegionList)
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void domainInsertChild() {
        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .asInsert();

        printStmt(LOG, stmt);

    }


    @Test
    public void valueInsertParent() {
        final ChinaRegion<?> r;
        r = new ChinaRegion<>()
                .setName("雪谷海沟")
                .setRegionGdp(new BigDecimal("666.88"))
                .setParentId(2343L);

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, r.getName())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, r.getRegionGdp())
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, "光明顶")
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void valueInsertChild() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, "武当山")
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, "6666.66")
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, "光明顶")
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T)
                .defaultValue(ChinaCity_.mayorName, SQLs::param, "")
                .values()

                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, "远浪舰长"))
                .comma()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, "远浪舰长"))
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void queryInsertParent() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                        .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.regionType)
                )
                .space()
                .select(HistoryChinaRegion_.id, HistoryChinaRegion_.createTime, HistoryChinaRegion_.updateTime, HistoryChinaRegion_.version)
                .comma(HistoryChinaRegion_.visible, HistoryChinaRegion_.name, HistoryChinaRegion_.regionGdp)
                .comma(SQLs.literalValue(RegionType.NONE)::as, HistoryChinaRegion_.REGION_TYPE)
                .from(HistoryChinaRegion_.T, AS, "r")
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);

    }


    @Test
    public void childTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                        .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.regionType)
                )
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalValue(RegionType.PROVINCE)::as, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "r")
                .asQuery()
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .parens(s -> s.space(ChinaProvince_.id, ChinaProvince_.governor))
                .space()
                .select(ChinaProvince_.id, ChinaProvince_.governor)
                .from(ChinaProvince_.T, AS, "c")
                .join(ChinaRegion_.T, AS, "p").on(ChinaProvince_.id::equal, ChinaRegion_.id)
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);
    }


    private List<ChinaRegion<?>> createRegionList() {
        List<ChinaRegion<?>> domainList = _Collections.arrayList();
        ChinaRegion<?> region;

        for (int i = 0; i < 3; i++) {
            region = new ChinaRegion<>();
            region.setId((long) i);
            region.setName("江湖" + i);
            domainList.add(region);
        }
        return domainList;
    }


}
