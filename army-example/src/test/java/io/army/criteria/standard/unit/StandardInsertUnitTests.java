package io.army.criteria.standard.unit;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
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


    @Test//(invocationCount = 100)
    public void domainInsertParent() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = SQLs.singleInsert()
//                .migration(true)
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.parentId).rightParen()
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

                .leftParen(ChinaRegion_.name, SQLs::param, r::getName)
                .comma(ChinaRegion_.regionGdp, SQLs::literal, r::getRegionGdp)
                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::param, "光明顶")
                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                .rightParen()
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

                .leftParen(ChinaRegion_.name, SQLs::literal, () -> "武当山")
                .comma(ChinaRegion_.regionGdp, SQLs::literal, () -> "6666.66")
                .comma(ChinaRegion_.parentId, SQLs::param, () -> 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, SQLs::literal, () -> "光明顶")
                .comma(ChinaRegion_.parentId, SQLs::param, () -> 0)
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

        printStmt(LOG, stmt);
    }

    @Test
    public void queryInsertParent() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration(true)
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.regionType)
                .rightParen()
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.NONE), AS, ChinaRegion_.REGION_TYPE)
                .from(ChinaRegion_.T, AS, "r")
                .asQuery()
                .asInsert();

        printStmt(LOG, stmt);

    }


    @Test
    public void singleTableSubQueryInsert() {
        final Insert stmt;
        stmt = SQLs.singleInsert()
                .migration(true)
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp, ChinaRegion_.regionType)
                .rightParen()
                .space()
                .select(ChinaRegion_.id, ChinaRegion_.createTime, ChinaRegion_.updateTime, ChinaRegion_.version)
                .comma(ChinaRegion_.visible, ChinaRegion_.name, ChinaRegion_.regionGdp)
                .comma(SQLs.literalFrom(RegionType.PROVINCE), AS, ChinaRegion_.REGION_TYPE)
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
                .join(ChinaRegion_.T, AS, "p").on(ChinaProvince_.id::equal, ChinaRegion_.id)
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


}
