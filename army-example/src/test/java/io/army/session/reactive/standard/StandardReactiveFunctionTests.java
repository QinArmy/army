/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.session.reactive.standard;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.mapping.StringType;
import io.army.reactive.ReactiveLocalSession;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.cases;

@Test(dataProvider = "localSessionProvider")
public class StandardReactiveFunctionTests extends SessionSupport {


    @Test
    public void simpleCaseFunc(final ReactiveLocalSession session) {
        final Select stmt;
        stmt = SQLs.query()
                .select(cases(SQLs.constValue(3))
                        .when(1)
                        .then(2)
                        .elseValue(0)
                        .end(StringType.INSTANCE).as("a")
                ).asQuery();

        final String result;
        result = session.queryOne(stmt, String.class)
                .block();
        Assert.assertNotNull(result);
    }

}
