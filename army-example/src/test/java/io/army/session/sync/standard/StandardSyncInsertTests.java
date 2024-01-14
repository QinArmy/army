package io.army.session.sync.standard;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(dataProvider = "localSessionProvider")
public class StandardSyncInsertTests extends StandardSyncSessionSupport {


    @Test
    public void insertParent(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList = createReginList();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(regionList)
                .asInsert();

        final long rows;
        rows = session.update(stmt);

        Assert.assertEquals(rows, regionList.size());

        assertChinaRegionAfterNoConflictInsert(regionList);

    }


    @Test
    public void insertChild(final SyncLocalSession session) {

        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> regionList = createProvinceList();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .defaultValue(ChinaRegion_.parentId, SQLs::literal, 0)
                .values(regionList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .defaultValue(ChinaProvince_.governor, SQLs::literal, randomPerson())
                .values(regionList)
                .asInsert();


        session.startTransaction();

        try {
            final long rows;
            rows = session.update(stmt);

            Assert.assertEquals(rows, regionList.size());

            assertChinaRegionAfterNoConflictInsert(regionList);
            session.commit();
        } catch (RuntimeException e) {
            session.rollback();
            throw e;
        }

    }


}
