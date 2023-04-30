package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.ExpressionElement;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.optional.PrimitiveIntArrayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.TRUE;

public class PostgreJsonFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreJsonFuncUnitTests.class);


    /**
     * @see Postgres#toJson(ExpressionElement)
     * @see Postgres#toJsonb(ExpressionElement)
     */
    @Test
    public void toJsonFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(toJson(SQLs.literalValue("Fred said \"Hi.\""))::as, "json1")
                .comma(toJsonb(SQLs.literalValue("Fred said \"Hi.\""))::as, "json2")
                .comma(toJson(row(SQLs.literalValue(1), SQLs.literalValue(2), row(SQLs.literalValue(randomPerson()))))::as, "json3")
                .comma(toJsonb(row(SQLs.literalValue(1), SQLs.literalValue(2), row(SQLs.literalValue(randomPerson()))))::as, "json4")
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
        final int[][] intArray = {{1, 5}, {99, 100}};
        final Select stmt;
        stmt = Postgres.query()
                .select(arrayToJson(SQLs.literal(PrimitiveIntArrayType.LINEAR, intArray))::as, "json1")
                .comma(arrayToJson(SQLs.literal(PrimitiveIntArrayType.LINEAR, arrayLiteral))::as, "json2")
                .comma(arrayToJson(SQLs.literal(PrimitiveIntArrayType.LINEAR, arrayLiteral), TRUE)::as, "json3")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#rowToJson(ExpressionElement)
     * @see Postgres#rowToJson(ExpressionElement, Expression)
     */
    @Test
    public void rowToJsonFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(rowToJson(row(SQLs.literalValue(1), SQLs.literalValue("zoro"))
                        )::as, "json1"
                )
                .comma(rowToJson(row(SQLs.literalValue(2), SQLs.literalValue("zoro")), TRUE
                        )::as, "json2"
                ).comma(rowToJson(row(SQLs.literalValue(3)), TRUE
                        )::as, "json3"
                ).comma(rowToJson(row(c -> c.space(SQLs.literalValue(randomPerson()))
                                .comma(SQLs.literalValue(randomPerson()))
                        )).as("json4")
                ).comma(rowToJson(row(c -> c.space(SQLs.literalValue(randomPerson()))
                                .comma(SQLs.literalValue(randomPerson()))
                        ), TRUE).as("json5")
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonBuildArray(ExpressionElement...)
     * @see Postgres#jsonBuildArray(Consumer)
     * @see Postgres#jsonbBuildArray(ExpressionElement...)
     * @see Postgres#jsonbBuildArray(Consumer)
     */
    @Test
    public void jsonBuildArrayFunc() {

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonBuildArray(SQLs.literalValue(1), SQLs.literalValue("zoro"))::as, "json1"
                ).comma(jsonBuildArray(c -> c.space(SQLs.literalValue(randomPerson()))
                                .comma(SQLs.literalValue(randomPerson()))
                        ).as("json2")
                )
                .comma(jsonbBuildArray(SQLs.literalValue(2), SQLs.literalValue("zoro"))::as, "json3"
                ).comma(jsonbBuildArray(c -> c.space(SQLs.literalValue(randomPerson()))
                                .comma(SQLs.literalValue(randomPerson()))
                        ).as("json4")
                )
                .asQuery();

        printStmt(LOG, stmt);

    }


}
