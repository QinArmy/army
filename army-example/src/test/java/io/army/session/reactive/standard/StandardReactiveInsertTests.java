package io.army.session.reactive.standard;


import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.reactive.ReactiveLocalSession;
import io.army.session.record.ResultStates;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Test(dataProvider = "localSessionProvider")
public class StandardReactiveInsertTests extends StandardReactiveSessionTestSupport {

    @Test
    public void domainInsert(final ReactiveLocalSession session) {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final List<ChinaRegion<?>> regionList;
        regionList = this.createReginList();

        final Insert stmt;
        stmt = SQLs.singleInsert()
                .literalMode(LiteralMode.LITERAL)
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


}
