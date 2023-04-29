package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static io.army.criteria.impl.Postgres.toJson;
import static io.army.criteria.impl.Postgres.toJsonb;

public class PostgreJsonFuncTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreJsonFuncTests.class);


    /**
     * @see Postgres#toJson(Expression)
     * @see Postgres#toJsonb(Expression)
     */
    @Test
    public void toJsonFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(toJson(SQLs.literalValue("Fred said \"Hi.\""))::as, "json1")
                .comma(toJsonb(SQLs.literalValue("Fred said \"Hi.\""))::as, "json3")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#arrayToJson(Expression, Expression)
     */
    @Test
    public void arrayToJsonFunc() {
        final String arrayLiteral;
        arrayLiteral = "{{1,5},{99,100}}";
//        final Select stmt;
//        stmt = Postgres.query()
//                .select(arrayToJson(SQLs.literal())::as, "json1")
//                .comma(toJson(SQLs::literal, "Fred said \"Hi.\"")::as, "json2")
//                .comma(toJsonb(SQLs.literalValue("Fred said \"Hi.\""))::as, "json3")
//                .comma(toJsonb(SQLs::literal, "Fred said \"Hi.\"")::as, "json4")
//                .asQuery();
//
//        printStmt(LOG, stmt);

    }


}
