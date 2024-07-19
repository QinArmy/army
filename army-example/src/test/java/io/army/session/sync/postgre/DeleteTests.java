package io.army.session.sync.postgre;

import io.army.criteria.Delete;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.session.SyncLocalSession;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static io.army.criteria.impl.SQLs.AS;


@Test(dataProvider = "localSessionProvider")
public class DeleteTests extends SessionTestSupport {


    @Transactional
    @Test(invocationCount = 3) // because first execution time contain class loading time and class initialization time
    public void deleteChildBeforeParent(final SyncLocalSession session) {
        final List<ChinaProvince> regionList = createProvinceListWithCount(6);
        session.batchSave(regionList);

        final List<Long> idList = extractRegionIdList(regionList);

        final long startNanoSecond = System.nanoTime();

        final Delete stmt;
        stmt = Postgres.singleDelete()
                .with("first_child_cte").as(sw -> sw.deleteFrom(ChinaProvince_.T, AS, "p")
                        .using(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(0, 3)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(SQLs.field("p", ChinaProvince_.id).as("myId"))
                        .asReturningDelete()
                ).comma("first_parent_cte").as(sw -> sw.deleteFrom(ChinaRegion_.T, AS, "c")
                        .using("first_child_cte")
                        .where(ChinaRegion_.id::equal, SQLs.refField("first_child_cte", "myId"))
                        .asDelete()
                ).comma("child_cte").as(sw -> sw.deleteFrom(ChinaProvince_.T, AS, "p")
                        .using(ChinaRegion_.T, AS, "c")
                        .where(ChinaProvince_.id.in(SQLs::rowParam, idList.subList(3, 6)))
                        .and(ChinaProvince_.id::equal, ChinaRegion_.id)
                        .returning(ChinaProvince_.id)
                        .asReturningDelete()
                ).space()
                .deleteFrom(ChinaRegion_.T, AS, "c")
                .using("child_cte")
                .where(ChinaRegion_.id::equal, SQLs.refField("child_cte", ChinaRegion_.ID))
                .asDelete();

        statementCostTimeLog(session, LOG, startNanoSecond);
        Assert.assertEquals(session.update(stmt), 3L);

    }


}
