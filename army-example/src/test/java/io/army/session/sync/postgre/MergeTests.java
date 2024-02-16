package io.army.session.sync.postgre;


import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.postgre.PostgreMerge;
import io.army.example.bank.domain.user.*;
import io.army.sync.SyncLocalSession;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static io.army.criteria.impl.SQLs.AS;
import static io.army.criteria.impl.SQLs.PERIOD;

@Test(dataProvider = "localSessionProvider")
public class MergeTests extends SessionTestSupport {


    @Test
    public void simple(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        final Random random = ThreadLocalRandom.current();
        final Captcha captcha = new Captcha()
                .setCaptcha(String.valueOf(random.nextLong()))
                .setRequestNo(String.valueOf(random.nextLong()) + random.nextLong())
                .setDeadline(LocalDateTime.now().plusMinutes(15))
                .setPartnerId(0L);

        final PostgreMerge stmt;
        stmt = Postgres.singleMerge()
                .with("parent_cte").as(sw -> sw.insertInto(ChinaRegion_.T)
                        .values(regionList)
                        .returning(ChinaRegion_.id)
                        .asReturningInsert()
                ).comma("parent_row_number").as(sw -> sw.select(s -> s.space(Postgres.rowNumber().over().as("rowNumber"))
                                        .comma(SQLs.refField("parent_cte", ChinaRegion_.ID))
                                ).from("parent_cte")
                                .asQuery()
                ).comma("child_cte").as(sw -> sw.insertInto(ChinaProvince_.T)
                        .defaultValue(ChinaProvince_.id, SQLs.scalarSubQuery()
                                .select(s -> s.space(SQLs.refField("parent_row_number", ChinaRegion_.ID)))
                                .from("parent_row_number")
                                .where(SQLs.refField("parent_row_number", "rowNumber")::equal, SQLs.BATCH_NO_LITERAL)
                                .asQuery()
                        ).values(regionList)
                        .asInsert()
                ).space()
                .mergeInto(Captcha_.T, AS, "c")
                .using(RegisterRecord_.T, AS, "r").on(Captcha_.requestNo::equal, RegisterRecord_.requestNo)
                .whenNotMatched().then(s -> s.insert()
                        .values()
                        .parens(r -> r.space(Captcha_.captcha, SQLs::param, captcha.getCaptcha())
                                .comma(Captcha_.deadline, SQLs::param, captcha.getDeadline())
                                .comma(Captcha_.requestNo, RegisterRecord_.requestNo)
                                .comma(Captcha_.partnerId, SQLs.field("r", RegisterRecord_.partnerId))
                        )
                ).asCommand();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("{} row : {}", session.name(), rows);

    }


    @Test
    public void subQuery(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(3);

        final Random random = ThreadLocalRandom.current();
        final Captcha captcha = new Captcha()
                .setCaptcha(String.valueOf(random.nextLong()))
                .setRequestNo(String.valueOf(random.nextLong()) + random.nextLong())
                .setDeadline(LocalDateTime.now().plusMinutes(15))
                .setPartnerId(0L);

        final PostgreMerge stmt;
        stmt = Postgres.singleMerge()
                .with("parent_cte").as(sw -> sw.insertInto(ChinaRegion_.T)
                        .values(regionList)
                        .returning(ChinaRegion_.id)
                        .asReturningInsert()
                ).comma("parent_row_number").as(sw -> sw.select(s -> s.space(Postgres.rowNumber().over().as("rowNumber"))
                                        .comma(SQLs.refField("parent_cte", ChinaRegion_.ID))
                                ).from("parent_cte")
                                .asQuery()
                ).comma("child_cte").as(sw -> sw.insertInto(ChinaProvince_.T)
                        .defaultValue(ChinaProvince_.id, SQLs.scalarSubQuery()
                                .select(s -> s.space(SQLs.refField("parent_row_number", ChinaRegion_.ID)))
                                .from("parent_row_number")
                                .where(SQLs.refField("parent_row_number", "rowNumber")::equal, SQLs.BATCH_NO_LITERAL)
                                .asQuery()
                        ).values(regionList)
                        .asInsert()
                ).space()
                .mergeInto(Captcha_.T, AS, "c")
                .using(SQLs.subQuery()
                        .select("t", PERIOD, RegisterRecord_.T)
                        .from(RegisterRecord_.T, AS, "t")
                        .orderBy(RegisterRecord_.id::desc)
                        .limit(SQLs::param, 100)
                        .asQuery()
                ).as("r").on(Captcha_.requestNo::equal, SQLs.refField("r", RegisterRecord_.REQUEST_NO))
                .whenNotMatched().then(s -> s.insert()
                        .values()
                        .parens(r -> r.space(Captcha_.captcha, SQLs::param, captcha.getCaptcha())
                                .comma(Captcha_.deadline, SQLs::param, captcha.getDeadline())
                                .comma(Captcha_.requestNo, SQLs.refField("r", RegisterRecord_.REQUEST_NO))
                                .comma(Captcha_.partnerId, SQLs.refField("r", RegisterRecord_.PARTNER_ID))
                        )
                )
                .asCommand();

        final long rows;
        rows = session.update(stmt);
        LOG.debug("{} row : {}", session.name(), rows);

    }

}
