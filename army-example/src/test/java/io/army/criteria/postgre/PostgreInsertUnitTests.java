package io.army.criteria.postgre;

import io.army.criteria.Insert;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.BankPerson;
import io.army.example.bank.domain.user.BankPerson_;
import io.army.example.bank.domain.user.BankUser_;
import io.army.example.bank.domain.user.ChinaRegion_;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgreInsertUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertUnitTests.class);


    @Test
    public void domainInsertParent() {
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
                .doNothing()
                .asInsert();


        printStmt(LOG, stmt);

    }


    @Test
    public void domainInsertChild() {
        final List<BankPerson> bankPersonList;
        bankPersonList = this.createBankPersonList();
        final ReturningInsert stmt;
        stmt = Postgres.singleInsert()
                .insertInto(BankUser_.T).as("u")
                .overridingSystemValue()
                .values(bankPersonList)
                .onConflict()
                .onConstraint("")
                .doNothing()
                .returningAll()
                .asReturningInsert()
                .child()

                .insertInto(BankPerson_.T)
                .values(bankPersonList)
                .returningAll()
                .asReturningInsert();
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
