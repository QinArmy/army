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

package io.army.criteria.standard.unit;

import io.army.annotation.UpdateMode;
import io.army.criteria.BatchUpdate;
import io.army.criteria.Expression;
import io.army.criteria.Update;
import io.army.criteria.UpdateStatement;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaProvince_;
import io.army.example.bank.domain.user.ChinaRegion;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.pill.domain.PillUser_;
import io.army.example.pill.struct.IdentityType;
import io.army.mapping.BigDecimalType;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Supplier;

import static io.army.criteria.impl.SQLs.*;

public class StandardUpdateUnitTests extends StandardUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(StandardUpdateUnitTests.class);


    @Test
    public void updateParent() {
        final BigDecimal addGdp = new BigDecimal("888.8");
        final Map<String, Object> map = _Collections.hashMap();
        map.put("firstId", (byte) 1);
        map.put("secondId", "3");

        final UpdateStatement stmt;
        stmt = SQLs.singleUpdate()
                .update(ChinaRegion_.T, AS, "c")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, addGdp)
                .where(ChinaRegion_.id::between, SQLs::literal, map.get("firstId"), AND, map.get("secondId"))
                .and(SQLs.bracket(ChinaRegion_.name.equal(SQLs::literal, "江湖")))
                .and(ChinaRegion_.regionGdp::plus, SQLs::param, addGdp, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void updateChild() {
        final BigDecimal gdpAmount = new BigDecimal("888.8");
        final ChinaRegion<?> criteria = new ChinaRegion<>();
        final Supplier<Expression> amountSupplier = () -> SQLs.literal(BigDecimalType.INSTANCE, gdpAmount);

        criteria.setRegionGdp(gdpAmount);
        final Update stmt;
        stmt = SQLs.domainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.regionGdp, amountSupplier)  // test method infer
                .set(ChinaRegion_.regionGdp, SQLs::negate) // test method infer
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.equal(SQLs::literal, 1))
                .and(ChinaRegion_.name::equal, SQLs::param, "江湖")
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate();

        printStmt(LOG, stmt);

    }

    @Test
    public void batchUpdateParent() {
        final BatchUpdate stmt;
        stmt = SQLs.batchSingleUpdate()
                .update(ChinaRegion_.T, AS, "p") // update only parent table field: ChinaRegion_.*
                .setSpace(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::namedParam)
                .where(ChinaRegion_.id::spaceEqual, SQLs::namedParam)
                .and(ChinaRegion_.regionGdp::spacePlus, SQLs::namedParam, Expression::greaterEqual, LITERAL_DECIMAL_0) // test method infer
                .and(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0) // test method infer
                .ifAnd(ChinaRegion_.regionGdp::plus, SQLs::namedParam, ChinaRegion_.REGION_GDP, Expression::greaterEqual, LITERAL_DECIMAL_0) // test method infer
                .and(ChinaRegion_.version::equal, SQLs::param, "0")
                .asUpdate()
                .namedParamList(this.createProvinceList());

        printStmt(LOG, stmt);

    }

    @Test
    public void batchUpdateChild() {
        final BigDecimal gdpAmount = new BigDecimal("888.8");

        final BatchUpdate stmt;
        stmt = SQLs.batchDomainUpdate()
                .update(ChinaProvince_.T, AS, "p")
                .set(ChinaRegion_.name, SQLs::param, "武侠江湖")
                .set(ChinaRegion_.regionGdp, SQLs::plusEqual, SQLs::param, gdpAmount)
                .where(ChinaRegion_.id.equal(SQLs::namedParam, ChinaRegion_.ID))
                .and(ChinaRegion_.name.equal(SQLs::namedParam, ChinaRegion_.NAME))
                .and(ChinaRegion_.regionGdp::plus, SQLs::literal, gdpAmount, Expression::greaterEqual, LITERAL_DECIMAL_0)
                .asUpdate()
                .namedParamList(this.createProvinceList());

        printStmt(LOG, stmt);

    }


    /**
     * @see io.army.annotation.UpdateMode
     */
    @Test(enabled = false)
    @Deprecated
    public void updateParentWithOnlyNullMode() {
        assert PillUser_.identityId.updateMode() == UpdateMode.ONLY_NULL;
        final UpdateStatement stmt;
        stmt = SQLs.singleUpdate()
                .update(PillUser_.T, AS, "u")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void updateOnlyParentField() {
        final Update stmt;
        stmt = SQLs.singleUpdate()
                .update(PillUser_.T, AS, "up")
                .set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                .set(PillUser_.identityId, SQLs::literal, 888)
                .set(PillUser_.nickName, SQLs::param, "令狐冲")
                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .asUpdate();

        printStmt(LOG, stmt);
    }

    @Test
    public void dynamicSetUpdateOnlyParentField() {
        final UpdateStatement stmt;
        stmt = SQLs.singleUpdate()
                .update(PillUser_.T, AS, "up")
                .sets(s -> s.set(PillUser_.identityType, SQLs::literal, IdentityType.PERSON)
                        .set(PillUser_.identityId, SQLs::literal, 888)
                        .set(PillUser_.nickName, SQLs::param, "令狐冲"))

                .where(PillUser_.id::equal, SQLs::literal, "1")
                .and(PillUser_.nickName::equal, SQLs::param, "zoro")
                .asUpdate();

        printStmt(LOG, stmt);
    }


}
