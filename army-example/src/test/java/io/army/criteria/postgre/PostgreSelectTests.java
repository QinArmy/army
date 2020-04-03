package io.army.criteria.postgre;

import com.example.domain.account.Account_;
import com.example.domain.account.BalanceAccount_;
import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

public class PostgreSelectTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreSelectTests.class);

    @Test
    public void select() {
        MyCriteria criteria = new MyCriteria();

        Select<MyCriteria> select = Postgres.specialSelect(criteria)

                .withRecursive(this::withQuery)
                .selectDistinct(this::selectOnExpList, Account_.acceptTime)
                .from(Account_.T, "t")
                .tableSample(this::tableSampleList)
                .join(BalanceAccount_.T, "b")
                .on(Account_.id.eq(BalanceAccount_.id))
                .where(SQLS.always())
                .groupBy(Account_.id.sort(false))
                .having(Account_.id.ge(3L))
                .window(this::windowList)
                .orderBy(Account_.id)
                .limit(10)
                .asSelect()

                .union(this::secondSelect)
                .intersect(this::secondSelect)
                .exceptAll(this::secondSelect);
    }

    private Select<MyCriteria> secondSelect(MyCriteria criteria) {
        return null;
    }

    private List<PostgreWindow> windowList(MyCriteria criteria) {
        return Collections.emptyList();
    }

    private List<PostgreWithQuery> withQuery(MyCriteria criteria) {
        return Collections.emptyList();
    }

    private List<Expression<?>> selectOnExpList(MyCriteria criteria) {
        return Collections.emptyList();
    }

    private Expression<?> tableSampleList(MyCriteria criteria) {
        return null;
    }
}
