package io.army.criteria.mysql;

import io.army.annotation.GeneratorType;
import io.army.criteria.Hint;
import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
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

public class MySQLInsertUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

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
        stmt = MySQLs.domainInsert()
                .preferLiteral(true)
                .insert(hintSupplier, Collections.singletonList(MySQLModifier.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.parentId)
                .rightParen()
                .defaultLiteral(ChinaRegion_.visible, true)
                .values(this::createReginList)
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
                .insert(hintSupplier, Collections.singletonList(MySQLModifier.HIGH_PRIORITY))
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


    /*-------------------below query insert tests -------------------*/

    @Test
    public void queryInsertParent() {
        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.queryInsert()
                .insert(hintSupplier, Collections.singletonList(MySQLModifier.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition()
                .leftParen("p1")
                .rightParen()
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.regionType)
                .comma(ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.parentId)
                .rightParen()
                .space(() -> MySQLs.subQuery()  //here just test,not real use case
                        .select(consumer -> {
                            consumer.accept(ChinaRegion_.id);
                            consumer.accept(ChinaRegion_.createTime);
                            consumer.accept(ChinaRegion_.updateTime);
                            consumer.accept(ChinaRegion_.version);

                            consumer.accept(ChinaRegion_.visible);
                            consumer.accept(ChinaRegion_.regionType);
                            consumer.accept(ChinaRegion_.name);
                            consumer.accept(ChinaRegion_.regionGdp);

                            consumer.accept(ChinaRegion_.parentId);
                        })
                        .from(ChinaRegion_.T, "c")
                        .limit(10)
                        .asQuery())
                .asInsert();

        printStmt(stmt);

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

                    .setName("海龟徒弟" + i)
                    .setRegionType(RegionType.NONE)
                    .setRegionGdp(BigDecimal.valueOf(i)).setParentId(i * 100L)

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
        _Insert parentStmt;
        for (MySQLDialect dialect : MySQLDialect.values()) {
            parser = _MockDialects.from(dialect);

            stmt = parser.insert(insert, Visible.ONLY_VISIBLE);
            sql = parser.printStmt(stmt, true);
            LOG.debug("{}:\n{}", dialect.name(), sql);


            if (insert instanceof _Insert._ChildInsert && !(insert instanceof _Insert._QueryInsert)) {
                assert stmt instanceof PairStmt;
                parentStmt = ((_Insert._ChildInsert) insert).parentStmt();
                assert parentStmt.table().id().generatorType() != GeneratorType.POST
                        || ((PairStmt) stmt).parentStmt() instanceof GeneratedKeyStmt;
            }

        }

    }


}
