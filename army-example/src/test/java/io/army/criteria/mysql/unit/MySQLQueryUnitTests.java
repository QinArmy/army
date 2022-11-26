package io.army.criteria.mysql.unit;

import io.army.criteria.Select;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.example.bank.domain.user.RegionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.SQLs.*;

public class MySQLQueryUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLQueryUnitTests.class);

    @Test
    public void referenceFunction() {
        final Select stmt;
        stmt = MySQLs.query()
                .select(MySQLs::cases, ChinaRegion_.regionType)
                .when(SQLs::literalFrom, RegionType.NONE)
                .then(SQLs::literalFrom, RegionType.NONE.name())
                .when(SQLs::literalFrom, RegionType.PROVINCE)
                .then(SQLs::literalFrom, RegionType.PROVINCE.name())
                .when(SQLs::literalFrom, RegionType.CITY)
                .then(SQLs::literalFrom, RegionType.CITY.name())
                .elseValue(NULL)
                .end().as(ChinaRegion_.REGION_TYPE)
                .comma(MySQLs::rowNumber).over().as("rowNumber")
                .comma(MySQLs::sum, ChinaRegion_.regionGdp).over(s -> s.partitionBy(ChinaRegion_.regionType)).as("gdpSum")
                .comma(MySQLs::sum, DISTINCT, ChinaRegion_.regionGdp).over(s -> s.partitionBy(ChinaRegion_.regionType)).as("distinctGdpSum")
                .comma(MySQLs::lag, ChinaRegion_.population, SQLs.literalFrom(1))
                .over("w", s -> s.orderBy(ChinaRegion_.id).rows().between().unboundedPreceding().and().currentRow()).as("lag2")
                .from(ChinaRegion_.T, AS, "cr")
                .where(ChinaRegion_.id::greatEqual, SQLs::literal, 10)
                .window("w").as(s -> s.partitionBy(ChinaRegion_.regionType).orderBy(ChinaRegion_.id, DESC))
                .limit(SQLs::literal, 1)
                .asQuery();

        print80Stmt(LOG, stmt);

    }


}
