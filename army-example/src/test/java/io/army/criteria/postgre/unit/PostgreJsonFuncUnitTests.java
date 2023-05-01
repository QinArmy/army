package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.ExpressionElement;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.mapping.optional.PrimitiveIntArrayType;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.SymbolPeriod;
import static io.army.criteria.impl.Postgres.SymbolSpace;
import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.*;

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
     * @see Postgres#jsonBuildArray(SymbolSpace, Consumer)
     * @see Postgres#jsonbBuildArray(ExpressionElement...)
     * @see Postgres#jsonbBuildArray(Consumer)
     * @see Postgres#jsonbBuildArray(SymbolSpace, Consumer)
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
                ).comma(jsonBuildArray(c -> c.space("c", PERIOD, ChinaRegion_.T))::as, "json5")
                .comma(jsonbBuildArray(c -> c.space("c", PERIOD, ChinaRegion_.T))::as, "json6")
                .comma(jsonBuildArray(c -> c.space("b", PERIOD, ASTERISK))::as, "json7")
                .comma(jsonbBuildArray(c -> c.space("b", PERIOD, ASTERISK))::as, "json8")
                .comma(jsonBuildArray(SPACE, c -> c.accept("c", PERIOD, ChinaRegion_.T))::as, "json9")
                .comma(jsonBuildArray(SPACE, c -> c.accept("b", PERIOD, ASTERISK))::as, "json10")
                .comma(jsonbBuildArray(SPACE, c -> c.accept("c", PERIOD, ChinaRegion_.T))::as, "json11")
                .comma(jsonbBuildArray(SPACE, c -> c.accept("b", PERIOD, ASTERISK))::as, "json12")
                .from(ChinaRegion_.T, AS, "c")
                .join(Postgres.subQuery()
                        .select(SQLs.literalValue(5)::as, "id")
                        .asQuery()
                ).as("b").on(SQLs.refThis("b", "id")::equal, ChinaRegion_.id)
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonBuildObject(ExpressionElement...)
     * @see Postgres#jsonBuildObject(Consumer)
     * @see Postgres#jsonBuildObject(SymbolSpace, Consumer)
     * @see Postgres#jsonBuildObject(String, SymbolPeriod, TableMeta)
     * @see Postgres#jsonbBuildObject(ExpressionElement...)
     * @see Postgres#jsonbBuildObject(Consumer)
     * @see Postgres#jsonbBuildObject(SymbolSpace, Consumer)
     * @see Postgres#jsonbBuildObject(String, SymbolPeriod, TableMeta)
     */
    @Test
    public void jsonBuildObjectFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonBuildObject()::as, "json1")
                .comma(jsonBuildObject(SQLs.literalValue("name"), SQLs.literalValue("zoro"))::as, "json2")
                .comma(jsonBuildObject(c -> c.space("name", SQLs.literalValue("zoro"))
                                .comma("region", ChinaRegion_.name)
                                .comma("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .comma("derivedFields", "b", PERIOD, ASTERISK)
                                .comma("regionFields", jsonBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json3")
                ).comma(jsonBuildObject(SPACE, c -> c.accept("name", SQLs.literalValue("zoro"))
                                .accept("region", ChinaRegion_.name)
                                .accept("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .accept("derivedFields", "b", PERIOD, ASTERISK)
                                .accept("regionFields", jsonBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json4")
                ).comma(jsonbBuildObject()::as, "json5")
                .comma(jsonbBuildObject(SQLs.literalValue("name"), SQLs.literalValue("zoro"))::as, "json6")
                .comma(jsonbBuildObject(c -> c.space("name", SQLs.literalValue("zoro"))
                                .comma("region", ChinaRegion_.name)
                                .comma("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .comma("derivedFields", "b", PERIOD, ASTERISK)
                                .comma("regionFields", jsonbBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json7")
                ).comma(jsonbBuildObject(SPACE, c -> c.accept("name", SQLs.literalValue("zoro"))
                                .accept("region", ChinaRegion_.name)
                                .accept("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .accept("derivedFields", "b", PERIOD, ASTERISK)
                                .accept("regionFields", jsonbBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json8")
                )
                .from(ChinaRegion_.T, AS, "c")
                .join(Postgres.subQuery()
                        .select(SQLs.literalValue(5)::as, "id")
                        .asQuery()
                ).as("b").on(SQLs.refThis("b", "id")::equal, ChinaRegion_.id)
                .asQuery();

        printStmt(LOG, stmt);
    }


}
