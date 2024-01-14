package io.army.session.sync.standard;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.*;
import io.army.sync.SyncLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Test(dataProvider = "localSessionProvider")
public class StandardSyncInsertTests extends StandardSyncSessionSupport {


    @Test
    public void domainInsertParent(final SyncLocalSession session) {

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
    public void domainInsertChild(final SyncLocalSession session) {

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

    /*-------------------below static VALUES insert -------------------*/

    @Test
    public void staticValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomProvince(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                )
                .asInsert();


        Assert.assertEquals(session.update(stmt), 2L);


    }

    @Test
    public void staticValuesInsertChild(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values()

                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::literal, randomRegion(random))
                        .comma(ChinaRegion_.parentId, SQLs::param, 0)
                )
                .asInsert()

                .child()

                .insertInto(ChinaCity_.T)
                .values()

                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .comma()
                .parens(s -> s.space(ChinaCity_.mayorName, SQLs::param, randomPerson(random)))
                .asInsert();


        session.startTransaction();

        try {
            Assert.assertEquals(session.update(stmt), 2L);
            session.commit();
        } catch (RuntimeException e) {
            session.rollback();
            throw e;
        }

    }

    @Test
    public void dynamicValuesInsertParent(final SyncLocalSession session) {
        final Random random = ThreadLocalRandom.current();

        final int rowCount = 2;

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.regionGdp, SQLs::literal, "88888.88")
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(r -> {
                    for (int i = 0; i < rowCount; i++) {
                        r.parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                                .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                                .comma(ChinaRegion_.parentId, SQLs::literal, 0)
                        );
                    }
                })
                .asInsert();

        Assert.assertEquals(session.update(stmt), rowCount);

    }


}
