package io.army.session.reactive.mysql;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.criteria.impl.inner._ReturningDml;
import io.army.example.bank.domain.user.*;
import io.army.reactive.ReactiveLocalSession;
import io.army.session.Isolation;
import io.army.session.TransactionOption;
import io.army.session.record.ResultStates;
import io.army.util.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MySQLReactiveInsertTests extends MySQLReactiveSessionTestsSupport {


    private static final Logger LOG = LoggerFactory.getLogger(MySQLReactiveInsertTests.class);


    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertParent(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values(regionList)
                .asInsert();


        final ResultStates states;
        states = session.update(stmt)
                .block();

        Assert.assertNotNull(states);

        Assert.assertEquals(states.affectedRows(), regionList.size());

        for (ChinaRegion<?> region : regionList) {
            Assert.assertNotNull(region.getId());
        }

    }


    @Test(groups = Groups.DOMAIN_INSERT)
    public void domainInsertChildWithTowStmtUpdateMode(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaProvince> provinceList;
        provinceList = this.createProvinceList();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .literalMode(LiteralMode.PREFERENCE)
                .insertInto(ChinaRegion_.T)
                .values(provinceList)
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values(provinceList)
                .asInsert();


        session.startTransaction()
                .flatMap(info -> session.update(stmt))
                .flatMap(states -> {
                    Assert.assertEquals(states.affectedRows(), provinceList.size());

                    for (ChinaProvince province : provinceList) {
                        Assert.assertNotNull(province.getId()); // database generated key
                    }
                    return session.commit();
                }).onErrorResume(error -> session.rollback()
                        .then(Mono.error(error))
                ).block();

    }

    /*-------------------below values syntax tests -------------------*/

    @Test(groups = Groups.VALUES_INSERT)
    public void staticValuesInsertParent(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;
        final Random random = ThreadLocalRandom.current();
        final Insert stmt;
        stmt = MySQLs.singleInsert()
                //.literalMode(LiteralMode.LITERAL)
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert();

        session.update(stmt)
                .doOnNext(states -> Assert.assertEquals(states.affectedRows(), 2))
                .block();

    }


    @Test(groups = Groups.VALUES_INSERT)
    public void valuesInsertChildWithTowStmtUpdateMode(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Random random = ThreadLocalRandom.current();

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .insertInto(ChinaRegion_.T)
                .defaultValue(ChinaRegion_.visible, SQLs::literal, Boolean.TRUE)
                .values()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion())
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                ).comma()
                .parens(s -> s.space(ChinaRegion_.name, SQLs::param, randomRegion(random))
                        .comma(ChinaRegion_.regionGdp, SQLs::literal, randomDecimal(random))
                        .comma(ChinaRegion_.parentId, SQLs::literal, random.nextInt())
                )
                .asInsert()

                .child()

                .insertInto(ChinaProvince_.T)
                .values()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                ).comma()
                .parens(s -> s.space(ChinaProvince_.governor, SQLs::param, randomPerson(random))
                        .comma(ChinaProvince_.provincialCapital, SQLs::literal, randomPerson(random))
                )
                .asInsert();

        Assert.assertFalse(stmt instanceof _ReturningDml);


        session.startTransaction(TransactionOption.option(Isolation.READ_COMMITTED))
                .flatMap(info -> session.update(stmt))
                .flatMap(states -> {
                    Assert.assertEquals(states.affectedRows(), 2);
                    return session.commit();
                }).onErrorResume(error -> session.rollback()
                        .then(Mono.error(error))
                ).block();

    }

    /*-------------------below query insert syntax-------------------*/

    @Test(groups = Groups.QUERY_INSERT)
    public void queryInsertParent(final ReactiveLocalSession session) {
        assert HistoryChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Insert stmt;
        stmt = MySQLs.singleInsert()
                .migration()
                .insertInto(HistoryChinaRegion_.T)
                .space()
                .select("c", PERIOD, ChinaRegion_.T)
                .from(ChinaRegion_.T, AS, "c")
                .where(ChinaRegion_.regionType::equal, SQLs::literal, RegionType.NONE)
                .and(SQLs::notExists, MySQLs.subQuery()
                        .select(HistoryChinaRegion_.id)
                        .from(HistoryChinaRegion_.T, AS, "h")
                        .where(HistoryChinaRegion_.id::equal, ChinaRegion_.id)
                        ::asQuery
                )
                .limit(SQLs::literal, 2)
                .asQuery()
                .asInsert();

        session.update(stmt)
                .doOnNext(states -> Assert.assertTrue(states.affectedRows() > 0))
                .block();


    }


}
