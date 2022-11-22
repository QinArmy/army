package io.army.criteria.standard.unit;

import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Database;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static io.army.criteria.impl.SQLs.AS;

public class StandardInsertUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardInsertUnitTests.class);


    @Test
    public void domainInsertParent() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
//                .migration(true)
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.regionGdp, ChinaRegion_.parentId)
                .rightParen()
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(this::createRegionList)
                .asInsert();

        printStmt(stmt);

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

        printStmt(stmt);

    }


    @Test
    public void valueInsertParent() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .leftParen(ChinaRegion_.name, SQLs::param, "武当山")
                .comma(ChinaRegion_.regionGdp, SQLs::literal, "6666.66")
                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, "光明顶")
                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                .rightParen()
                .asInsert();

        printStmt(stmt);
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

                .leftParen(ChinaRegion_.name, SQLs::literal, "武当山")
                .comma(ChinaRegion_.regionGdp, SQLs::literal, "6666.66")
                .comma(ChinaRegion_.parentId, SQLs::param, 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::literal, "光明顶")
                .comma(ChinaRegion_.parentId, SQLs::param, 0)
                .rightParen()
                .asInsert()
                .child()

                .insertInto(ChinaCity_.T)
                .defaultValue(ChinaCity_.mayorName, SQLs::param, "")
                .values()

                .leftParen(ChinaCity_.mayorName, SQLs::param, "远浪舰长")
                .rightParen()

                .leftParen(ChinaCity_.mayorName, SQLs::literal, "远浪舰长")
                .rightParen()

                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void queryInsertParent() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .rightParen()

                .space()

                .select(ChinaRegion_.id, ChinaRegion_.createTime)
                .from(ChinaRegion_.T, AS, "r")
                .asQuery()
                .asInsert();

        printStmt(stmt);

    }


    @Test
    public void singleTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.regionType, ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.regionGdp)
                .comma(SQLs::literalFrom, () -> RegionType.CITY, AS, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "r")
                .asQuery()
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .leftParen(ChinaProvince_.id, ChinaProvince_.governor)
                .rightParen()
                .space()
                .select(ChinaProvince_.id, ChinaProvince_.governor)
                .from(ChinaProvince_.T, AS, "c")
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);
    }

    @Test
    public void childTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)

                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime)
                .comma(ChinaRegion_.updateTime, ChinaRegion_.regionType)
                .comma(ChinaRegion_.regionGdp)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.CITY), AS, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, SQLs.AS, "r")
                .asQuery()
                .asInsert()
                .child()

                .insertInto(ChinaCity_.T)
                .leftParen(ChinaCity_.id, ChinaCity_.mayorName)
                .rightParen()
                // below sub query is test case,not real.
                .space()
                .select(ChinaCity_.id, ChinaCity_.mayorName)
                .from(ChinaCity_.T, AS, "r")
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);
    }


    private List<ChinaRegion<?>> createRegionList() {
        List<ChinaRegion<?>> domainList = new ArrayList<>();
        ChinaRegion<?> region;

        for (int i = 0; i < 3; i++) {
            region = new ChinaRegion<>();
            region.setId((long) i);
            region.setName("江湖" + i);
            domainList.add(region);
        }
        return domainList;
    }

    private List<ChinaProvince> createProvinceList() {
        List<ChinaProvince> domainList = new ArrayList<>();
        ChinaProvince p;
        for (int i = 0; i < 3; i++) {
            p = new ChinaProvince();
            p.setId((long) i);
            p.setName("江湖" + i);
            p.setGovernor("盟主");
            p.setRegionGdp(new BigDecimal("6666.88"));
            p.setProvincialCapital("总堂");
            domainList.add(p);
        }
        return domainList;
    }


    private static void printStmt(final Insert insert) {
        String sql;
        for (Database database : Database.values()) {
            for (Dialect dialect : database.dialects()) {
                sql = insert.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
                // sql = insert.toString();
                //insert.mockAsStmt(dialect, Visible.ONLY_VISIBLE);
                LOG.debug("{}:\n{}", dialect.name(), sql);
            }
        }


    }


}
