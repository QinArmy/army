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

package io.army.criteria.postgre.statement;

import io.army.criteria.ErrorChildInsertException;
import io.army.criteria.IllegalTwoStmtModeException;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.postgre.Postgres;
import io.army.criteria.standard.SQLs;
import io.army.example.bank.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class InsertUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(InsertUnitTests.class);


    @Test
    public void domainInsertParentPost() {
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .ignoreReturnIds()
                .insertInto(ChinaRegion_.T).as("c")
                .overridingSystemValue()
                .values(this::createReginList)
                .onConflict()
                .parens(s -> s.space(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                        .comma(ChinaRegion_.createTime).space("timestamp_ops")
                )
                .where(ChinaRegion_.parentId.less(SQLs::literal, 1))
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void domainReturnInsertParentPost() {
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .ignoreReturnIds()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(this::createReginList)
                .onConflict()
                .parens(s -> s.space(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                        .comma(ChinaRegion_.createTime).space("timestamp_ops")
                )
                .where(ChinaRegion_.parentId.less(SQLs::literal, 1))
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returning(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.parentId)
                .asReturningInsert();

        printStmt(LOG, stmt);
    }


    @Test
    public void domainInsertOneChildPost() {
        final ChinaCity city;
        city = createCityListWithCount(1).get(0);
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .value(city)
                .onConflict()
                .parens(s -> s.space(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                        .comma(ChinaRegion_.createTime).space("timestamp_ops")
                )
                .where(ChinaRegion_.parentId.less(SQLs::literal, 1))
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .value(city)
                .asInsert();

        printStmt(LOG, stmt, Visible.BOTH);
    }

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void domainInsertChildPostWithParentDoNothing() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();

        Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .onConflict()
                .doNothing()   // here , couldn't use DO NOTHING clause, because child insert row count will error.
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .values(cityList)
                .onConflict()
                .onConstraint("id")
                .doUpdate()
                .set(ChinaCity_.mayorName, Postgres::excluded)
                .asInsert();

    }

    @Test
    public void domainInsertChildPostWithChildDoNothing() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .values(cityList)
                .onConflict()
                .doNothing()
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void domainReturningInsertChildPost() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .returningAll()
                .asReturningInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .values(cityList)
                .returningAll()
                .asReturningInsert();

        printStmt(LOG, stmt);
    }


    @Test
    public void domainInsertChild() {
        final List<BankPerson> bankPersonList;
        bankPersonList = this.createBankPersonListWithCount(3);

        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(BankUser_.T).as("u")
                .overridingSystemValue()
                .values(bankPersonList)
                .asInsert()

                .child()

                .insertInto(BankPerson_.T)
                .values(bankPersonList)
                .asInsert();

        printStmt(LOG, stmt);

    }


    @Test(expectedExceptions = IllegalTwoStmtModeException.class)
    public void illegalTwoStmtMode() {
        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        try {
            Postgres.singleInsert()
                    .insertInto(ChinaRegion_.T)
                    .values(provinceList)
                    .returningAll()
                    .asReturningInsert()  // first statement exists RETURNING clause

                    .child()

                    .insertInto(ChinaProvince_.T)
                    .values(provinceList)
                    .asInsert();            // second statement not exists RETURNING clause

            Assert.fail();
        } catch (IllegalTwoStmtModeException e) {
            LOG.debug(e.getMessage());
            throw e;
        }

    }




}
