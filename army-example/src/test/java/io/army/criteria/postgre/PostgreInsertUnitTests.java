package io.army.criteria.postgre;

import io.army.criteria.ErrorChildInsertException;
import io.army.criteria.Insert;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgreInsertUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertUnitTests.class);


    @Test
    public void domainInsertParentPost() {
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(this::createReginList)
                .onConflict()
                .leftParen(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                .comma(ChinaRegion_.createTime).space("timestamp_ops")
                .rightParen()
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
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(this::createReginList)
                .onConflict()
                .leftParen(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                .comma(ChinaRegion_.createTime).space("timestamp_ops")
                .rightParen()
                .where(ChinaRegion_.parentId.less(SQLs::literal, 1))
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
                .returning(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.parentId)
                .asReturningInsert();

        printStmt(LOG, stmt);
    }


    @Test
    public void domainInsertChildPost() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .onConflict()
                .leftParen(ChinaRegion_.parentId).collation("de_DE").space("int8_bloom_ops")
                .comma(ChinaRegion_.createTime).space("timestamp_ops")
                .rightParen()
                .where(ChinaRegion_.parentId.less(SQLs::literal, 1))
                .doUpdate()
                .set(ChinaRegion_.name, Postgres::excluded)
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

        printStmt(LOG, stmt);
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

    @Test(expectedExceptions = ErrorChildInsertException.class)
    public void domainInsertChildPostWithChildDoNothing() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();

        Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .values(cityList)
                .onConflict()
                .doNothing()   // here , couldn't use DO NOTHING clause, because child insert row count will error.
                .asInsert();

    }

    @Test
    public void domainReturningInsertChildPost() {
        final List<ChinaCity> cityList;
        cityList = this.createCityList();
        final Insert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(ChinaRegion_.T).as("cr")
                .overridingSystemValue()
                .values(cityList)
                .returning(ChinaRegion_.id)
                .asReturningInsert()

                .child()

                .insertInto(ChinaCity_.T).as("cc")
                .overridingUserValue()
                .values(cityList)
                .asInsert();

        printStmt(LOG, stmt);
    }


    @Test
    public void domainInsertChild() {
        final List<BankPerson> bankPersonList;
        bankPersonList = this.createBankPersonList();

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


    private List<BankPerson> createBankPersonList() {
        final List<BankPerson> list = new ArrayList<>();
        BankPerson u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new BankPerson();


            u.setCreateTime(now);
            u.setUpdateTime(now);

            u.setNickName("妖侠" + 1);

            list.add(u);

        }
        return list;
    }


}
