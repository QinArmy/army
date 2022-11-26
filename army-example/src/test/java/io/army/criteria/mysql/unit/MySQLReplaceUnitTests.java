package io.army.criteria.mysql.unit;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.*;
import io.army.stmt.GeneratedKeyStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MySQLReplaceUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLReplaceUnitTests.class);


    @Test
    public void domainReplaceParentPrecede() {
        assert BankUser_.id.generatorType() == GeneratorType.PRECEDE;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("userBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.PREFERENCE)
                .replace(hintSupplier, Collections.singletonList(MySQLs.DELAYED))
                .into( BankUser_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .leftParen(BankUser_.nickName, BankUser_.certificateId)
                .comma(BankUser_.registerRecordId)
                .rightParen()
                .defaultValue(BankUser_.visible, SQLs::literal, true)
                .values(this::createUserList)
                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void domainInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.PREFERENCE)
                .replace(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .into(ChinaRegion_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.parentId)
                .rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .asInsert();

        printStmt(stmt);

    }


    private List<BankUser<?>> createUserList() {
        final List<BankUser<?>> list = new ArrayList<>();
        BankUser<?> u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            u = new BankUser<>()
                    .setId((long) i)
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setNickName("妖侠" + i)
                    .setUserType(BankUserType.BANK)
                    .setPartnerUserId(0L)
                    .setCompleteTime(now.minusDays(1))

                    .setCertificateId(0L)
                    .setUserNo(Integer.toString(i + 9999))
                    .setRegisterRecordId(0L)
                    .setVersion(0)

                    .setVisible(Boolean.TRUE);

            list.add(u);
        }
        return list;
    }


    private List<ChinaRegion<?>> createReginList() {
        final List<ChinaRegion<?>> list = new ArrayList<>();
        ChinaRegion<?> c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaRegion<>()
                    .setId((long) i)
                    .setCreateTime(now)
                    .setUpdateTime(now)

                    .setName("武林" + i)
                    .setRegionType(RegionType.NONE)
                    .setRegionGdp(BigDecimal.valueOf(i * 100L))
                    .setParentId(i * 100L)

                    .setVersion(0)
                    .setVisible(Boolean.TRUE);

            list.add(c);
        }
        return list;
    }


    private void printStmt(final Insert insert) {
        String sql;
        DialectParser parser;
        Stmt stmt;
        for (MySQLDialect dialect : MySQLDialect.values()) {

            parser = _MockDialects.from(dialect);
            stmt = parser.insert(insert, Visible.ONLY_VISIBLE);
            sql = parser.printStmt(stmt, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);


            if (insert instanceof _Insert._ChildInsert && !(insert instanceof _Insert._QueryInsert)) {
                assert stmt instanceof PairStmt;
                assert !(((PairStmt) stmt).firstStmt() instanceof GeneratedKeyStmt);
            }

        }

    }


}
