package io.army.criteria.mysql;

import io.army.criteria.Hint;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MySQLInsertUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

    @Test
    public void domainInsertParentPost() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.domainInsert()
                .preferLiteral(true)
                .insert(hintSupplier, Collections.singletonList(MySQLWords.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.parentId)
                .rightParen()
                .defaultLiteral(ChinaRegion_.visible, true)
                .values(this::addChinaRegin)
                .onDuplicateKey()
                .update(ChinaRegion_.name, "光明顶")
                .commaLiteral(ChinaRegion_.regionGdp, "6666.88")
                .asInsert();

        printStmt(stmt);

    }


    @Test
    public void assignmentInsertParentPost() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.assignmentInsert()
                .preferLiteral(false)
                .insert(hintSupplier, Collections.singletonList(MySQLWords.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .set(ChinaRegion_.name, "光明顶")
                .setLiteral(ChinaRegion_.regionGdp, "6666.88")
                .setLiteral(ChinaRegion_.parentId, 0)
                .onDuplicateKey()
                .update(ChinaRegion_.name, "光明顶")
                .commaLiteral(ChinaRegion_.regionGdp, "6666.88")
                .asInsert();

        printStmt(stmt);

    }


    private void addChinaRegin(final Consumer<ChinaRegion<?>> consumer) {
        ChinaRegion<?> c;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < rowSize; i++) {
            c = new ChinaRegion<>()
                    .setId((long) i)
                    .setCreateTime(now).setUpdateTime(now)

                    .setName("海龟徒弟" + i)
                    .setRegionType(RegionType.NONE)
                    .setRegionGdp(BigDecimal.valueOf(i)).setParentId(i * 100L)

                    .setVersion(0)
                    .setVisible(Boolean.TRUE);

            consumer.accept(c);
        }

    }


    private void printStmt(Insert insert) {
        String sql;
        for (Dialect dialect : Dialect.values()) {
            sql = insert.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
