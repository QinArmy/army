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

package io.army.criteria.dialect;

import io.army.criteria.DefiniteExpression;
import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;

import java.util.function.BiFunction;


/**
 * <p>This interface representing variable expression.
 * <p>Example :
 * <pre>
 *     <code><br/>
 *    &#64;Test
 *    public void rowNumber(final SyncLocalSession session) {
 *        final List&lt;ChinaRegion&lt;?>> regionList = createReginListWithCount(10);
 *        session.batchSave(regionList);
 *
 *        final Select stmt;
 *        stmt = MySQLs.query()
 *                .select(s -> s.space(MySQLs.at("my_row_number").increment().as("rowNumber"))
 *                        .comma("t", PERIOD, ChinaRegion_.T)
 *                )
 *                .from(ChinaRegion_.T, AS, "t")
 *                .crossJoin(SQLs.subQuery()
 *                        .select(MySQLs.at("my_row_number", SQLs.COLON_EQUAL, SQLs.LITERAL_0).as("n"))
 *                        .asQuery()
 *                ).as("s")
 *                .where(ChinaRegion_.id.in(SQLs::rowLiteral, extractRegionIdList(regionList)))
 *                .orderBy(ChinaRegion_.id)
 *                .asQuery();
 *
 *        final List&lt;Map&lt;String, Object>> rowList;
 *        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
 *        final int rowSize = rowList.size();
 *        Assert.assertEquals(rowSize, regionList.size());
 *
 *        for (int i = 0; i &lt; rowSize; i++) {
 *            Assert.assertEquals(rowList.get(i).get("rowNumber"), i + 1);
 *        }
 *    }
 *     </code>
 * </pre>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/assignment-operators.html#operator_assign-value">Assignment Operators</a>
 */
public interface VarExpression extends DefiniteExpression {

    /**
     * @return session variable name
     */
    String name();


    SimpleExpression increment();

    SimpleExpression decrement();

    SimpleExpression assignment(Expression value);

    SimpleExpression plusEqual(Expression value);

    SimpleExpression minusEqual(Expression value);

    SimpleExpression timesEqual(Expression value);

    SimpleExpression divideEqual(Expression value);

    SimpleExpression modeEqual(Expression value);


    /*-------------------below -------------------*/

    <T> SimpleExpression assignment(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression plusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression minusEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression timesEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression divideEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);

    <T> SimpleExpression modeEqual(BiFunction<SimpleExpression, T, Expression> funcRef, T value);


}
