package io.army.criteria.mysql;

import io.army.annotation.GeneratorType;
import io.army.criteria.CriteriaException;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLFunctions;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.DialectParser;
import io.army.dialect._MockDialects;
import io.army.dialect.mysql.MySQLDialect;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import io.army.example.pill.domain.PillPerson;
import io.army.example.pill.domain.PillPerson_;
import io.army.example.pill.domain.PillUser;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.IdentityType;
import io.army.example.pill.struct.PillUserType;
import io.army.stmt.GeneratedKeyStmt;
import io.army.stmt.PairStmt;
import io.army.stmt.Stmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.AS;

public class MySQLInsertUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLInsertUnitTests.class);

    @Test
    public void domainInsertParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .asInsert();

        printStmt(LOG, stmt);

    }

    @Test
    public void domainInsertChildPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, MySQLFunctions::values)
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test(expectedExceptions = CriteriaException.class)
    public void domainInsertChildPostWithParentConflictError() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaRegion_.name, MySQLFunctions::values)
                .comma(ChinaRegion_.regionGdp, SQLs::plusEqual, MySQLFunctions.values(ChinaRegion_.regionGdp))
                .asInsert()
                .child()
                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.governor, ChinaProvince_.provincialCapital).rightParen()
                .values(provinceList)
                .onDuplicateKey()
                .update(ChinaProvince_.governor, MySQLFunctions::values)
                .comma(ChinaProvince_.provincialCapital, MySQLFunctions::values)
                .asInsert();

        printStmt(LOG, stmt);
    }

    //@Test
    public void domainInsertChild() {
        assert ChinaRegion_.id.generatorType() != GeneratorType.POST;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = new ArrayList<>();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        final List<PillPerson> pillPersonList;
        pillPersonList = this.createPsersonList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, Collections.singletonList(MySQLs.HIGH_PRIORITY))
                .into(PillUser_.T)
                .partition("p1")
                .defaultValue(PillUser_.visible, SQLs::literal, true)
                .values(pillPersonList)
                .onDuplicateKey()
                .update(PillUser_.identityId, SQLs::param, 0)
                .asInsert()

                .child()

                .insertInto(PillPerson_.T)
                .defaultValue(PillPerson_.birthday, SQLs::literal, LocalDate.now())
                .values(pillPersonList)
                .onDuplicateKey()
                .update(PillPerson_.birthday, SQLs::param, 0)
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
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insert(hintSupplier, Collections.singletonList(MySQLs.HIGH_PRIORITY))
                .into(ChinaRegion_.T)
                .partition("p1")
                .set(ChinaRegion_.name, SQLs::param, "光明顶")
                .set(ChinaRegion_.regionGdp, SQLs::param, "6666.88")
                .set(ChinaRegion_.parentId, SQLs::literal, 0)
                .onDuplicateKey()
                .update(ChinaRegion_.name, SQLs::param, "光明顶")
                .comma(ChinaRegion_.regionGdp, SQLs::param, "6666.88")
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

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insert(hintSupplier, Collections.emptyList())
                .into(ChinaRegion_.T)
                .partition("p1")
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(ChinaRegion_.regionType)
                .rightParen()
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.parentId, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.NONE), AS, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "c")
                .limit(SQLs::param, 10)
                .asQuery()
                .asInsert();

        printStmt(stmt);

    }


    final List<PillPerson> createPsersonList() {
        final List<PillPerson> list = new ArrayList<>();
        PillPerson u;
        final int rowSize = 3;
        final LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < rowSize; i++) {
            u = new PillPerson();

            u.setIdentityId(i + 1L);
            u.setCreateTime(now);
            u.setUpdateTime(now);
            u.setUserType(PillUserType.NONE);

            u.setIdentityType(IdentityType.PERSON);
            u.setNickName("脉兽" + 1);
            u.setBirthday(LocalDate.now());

            list.add(u);

        }
        return list;
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
            u.setNickName("脉兽" + 1);

            list.add(u);

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
                        || ((PairStmt) stmt).firstStmt() instanceof GeneratedKeyStmt;
            }

        }

    }


}
