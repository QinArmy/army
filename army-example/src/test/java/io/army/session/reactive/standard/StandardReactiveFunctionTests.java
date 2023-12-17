package io.army.session.reactive.standard;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.reactive.ReactiveLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.cases;

@Test(dataProvider = "localSessionProvider")
public class StandardReactiveFunctionTests extends StandardReactiveSessionTestSupport {


    @Test
    public void simpleCaseFunc(final ReactiveLocalSession session) {
        final Select stmt;
        stmt = SQLs.query()
                .select(cases(3)
                        .when(1)
                        .then(2)
                        .elseValue(0)
                        .end().as("a")
                ).asQuery();

        final String result;
        result = session.queryOne(stmt, String.class)
                .block();
        Assert.assertNotNull(result);
    }

}
