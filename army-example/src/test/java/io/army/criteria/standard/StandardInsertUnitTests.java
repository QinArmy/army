package io.army.criteria.standard;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.criteria.impl.SQLs;
import io.army.dialect.Dialect;
import io.army.example.bank.domain.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StandardInsertUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardInsertUnitTests.class);


    @Test
    public void domainInsertParent() {
        final Insert stmt;
        stmt = SQLs.domainInsert()
//                .migration(true)
                .preferLiteral(true)
                .insertInto(ChinaRegion_.T)
                .leftParen(ChinaRegion_.regionGdp, ChinaRegion_.parentId)
                .rightParen()
                .defaultLiteral(ChinaRegion_.regionGdp, "88888.88")
                .defaultLiteral(ChinaRegion_.visible, true)
                .defaultLiteral(ChinaRegion_.parentId, 0)
                .values(this::createRegionList)
                .asInsert();

        printStmt(stmt);

    }

    @Test
    public void domainInsertChild() {

        Insert stmt;
        stmt = SQLs.domainInsert()
                .preferLiteral(true)
                .insertInto(ChinaRegion_.T)
                .child()
                .insertInto(ChinaProvince_.T)
                .values(this::createProvinceList)
                .asInsert();

        printStmt(stmt);

    }


    @Test
    public void valueInsertParent() {
        final Insert stmt;
        stmt = SQLs.valueInsert()
                .preferLiteral(true)
                .insertInto(ChinaRegion_.T)
                .defaultLiteral(ChinaRegion_.regionGdp, "88888.88")
                .defaultLiteral(ChinaRegion_.visible, true)
                .values()

                .leftParen(ChinaRegion_.name, "武当山")
                .commaLiteral(ChinaRegion_.regionGdp, "6666.66")
                .comma(ChinaRegion_.parentId, 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, "光明顶")
                .comma(ChinaRegion_.parentId, 0)
                .rightParen()
                .asInsert();

        printStmt(stmt);
    }

    @Test
    public void valueInsertChild() {
        final Insert stmt;
        stmt = SQLs.valueInsert()
                .preferLiteral(true)
                .insertInto(ChinaRegion_.T)
                .defaultLiteral(ChinaRegion_.regionGdp, "88888.88")
                .defaultLiteral(ChinaRegion_.visible, true)
                .values()

                .leftParen(ChinaRegion_.name, "武当山")
                .commaLiteral(ChinaRegion_.regionGdp, "6666.66")
                .comma(ChinaRegion_.parentId, 0)
                .rightParen()

                .leftParen(ChinaRegion_.name, "光明顶")
                .comma(ChinaRegion_.parentId, 0)
                .rightParen()

                .child()

                .insertInto(ChinaCity_.T)
                .defaultValue(ChinaCity_.mayorName, "")
                .values()

                .leftParen(ChinaCity_.mayorName, "远浪舰长")
                .rightParen()

                .leftParen(ChinaCity_.mayorName, "远浪舰长")
                .rightParen()

                .asInsert();

        printStmt(stmt);
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
        for (Dialect dialect : Dialect.values()) {
            sql = insert.mockAsString(dialect, Visible.ONLY_VISIBLE, true);
            // sql = insert.toString();
            //insert.mockAsStmt(dialect, Visible.ONLY_VISIBLE);
            LOG.debug("{}:\n{}", dialect.name(), sql);
        }

    }


}
