package io.army.criteria.postgre;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.dialect.ReturningInsert;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.dialect.postgre.PostgreDialect;
import io.army.example.bank.domain.user.BankPerson;
import io.army.example.bank.domain.user.BankPerson_;
import io.army.example.bank.domain.user.BankUser_;
import io.army.example.pill.domain.PillUser;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.PillUserType;
import io.army.stmt.GeneratedKeyStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostgreInsertUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreInsertUnitTests.class);


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


    private List<PillUser> createUserList() {
        final List<PillUser> list = new ArrayList<>();
        PillUser u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillUser();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("妖侠" + 1);

            list.add(u);

        }
        return list;
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


    private static void printStmt(final Insert insert) {
        DialectParser parser;
        Stmt stmt;
        String sql;
        _Insert parentStmt;
        for (PostgreDialect dialect : PostgreDialect.values()) {
            parser = _MockDialects.from(dialect);
            stmt = parser.insert(insert, Visible.ONLY_VISIBLE);
            sql = parser.printStmt(stmt, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);


            if (insert instanceof _Insert._ChildInsert && !(insert instanceof _Insert._QueryInsert)) {
                assert stmt instanceof PairStmt;
                parentStmt = ((_Insert._ChildInsert) insert).parentStmt();
                assert parentStmt.table().id().generatorType() != GeneratorType.POST
                        || ((PairStmt) stmt).firstStmt() instanceof GeneratedKeyStmt;
            }
        }


    }


}
