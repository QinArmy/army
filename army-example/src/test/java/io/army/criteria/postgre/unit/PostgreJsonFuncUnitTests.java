package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.RowElement;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.criteria.postgre.mapping.MyRowType;
import io.army.criteria.postgre.mapping.MySubRowType;
import io.army.criteria.postgre.mapping.TwoIntType;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.mapping.*;
import io.army.mapping.array.IntegerArrayType;
import io.army.mapping.array.PrimitiveIntArrayType;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.optional.JsonPathType;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.SymbolPeriod;
import static io.army.criteria.impl.Postgres.SymbolSpace;
import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.row;
import static io.army.criteria.impl.SQLs.*;

public class PostgreJsonFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreJsonFuncUnitTests.class);


    /**
     * @see Postgres#toJson(Object)
     * @see Postgres#toJsonb(Object)
     */
    @Test
    public void toJsonFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(toJson(SQLs.literalValue("Fred said \"Hi.\""))::as, "json1")
                .comma(toJsonb(SQLs.literalValue("Fred said \"Hi.\""))::as, "json2")
                .comma(toJson(row(1, SQLs.literalValue(1), SQLs.literalValue(2), row(SQLs.literalValue(randomPerson()))))::as, "json3")
                .comma(toJsonb(row(SQLs.literalValue(1), SQLs.literalValue(2), row(SQLs.literalValue(randomPerson()))))::as, "json4")
                .comma(toJsonb(SQLs.space("b", PERIOD, ASTERISK))::as, "json5")
                .from(Postgres.subQuery()
                        .select(ChinaRegion_.id, ChinaRegion_.name)
                        .from(ChinaRegion_.T, AS, "c")
                        .limit(SQLs::literal, 10)
                        ::asQuery
                ).as("b")
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
     * @see Postgres#rowToJson(RowElement)
     * @see Postgres#rowToJson(RowElement, Expression)
     */
    @Test
    public void rowToJsonFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(rowToJson(row(SQLs.literalValue(1), SQLs.literalValue("zoro"))
                        )::as, "json1"
                ).comma(rowToJson(row(SQLs.literalValue(2), SQLs.literalValue("zoro")), TRUE
                        )::as, "json2"
                ).comma(rowToJson(row(SQLs.literalValue(3)), TRUE
                        )::as, "json3"
                ).comma(rowToJson(row(c -> {
                                    c.accept(SQLs.literalValue(randomPerson()));
                                    c.accept(SQLs.literalValue(randomPerson()));
                                }
                        )).as("json4")
                ).comma(rowToJson(row(c -> {
                                    c.accept(SQLs.literalValue(randomPerson()));
                                    c.accept(SQLs.literalValue(randomPerson()));
                                }
                        ), TRUE).as("json5")
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonBuildArray(Object)
     * @see Postgres#jsonBuildArray(Consumer)
     * @see Postgres#jsonBuildArray(Object)
     * @see Postgres#jsonbBuildArray(Consumer)
     */
    @Test
    public void jsonBuildArrayFunc() {

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonBuildArray(SQLs.literalValue(1), SQLs.literalValue("zoro"))::as, "json1")
                .comma(jsonbBuildArray(SQLs.literalValue(2), SQLs.literalValue("zoro"))::as, "json3")
                .comma(jsonBuildArray(space("b", PERIOD, ASTERISK))::as, "json10")
                .comma(jsonbBuildArray(space("b", PERIOD, ASTERISK))::as, "json12")
                .comma(jsonbBuildArray(c -> {

                }).as("json13"))
                .from(ChinaRegion_.T, AS, "c")
                .join(Postgres.subQuery()
                        .select(SQLs.literalValue(5)::as, "id")
                        .asQuery()
                ).as("b").on(SQLs.refThis("b", "id")::equal, ChinaRegion_.id)
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonBuildObject(Consumer)
     * @see Postgres#jsonBuildObject(SymbolSpace, Consumer)
     * @see Postgres#jsonBuildObject(String, SymbolPeriod, TableMeta)
     * @see Postgres#jsonbBuildObject(Consumer)
     * @see Postgres#jsonbBuildObject(SymbolSpace, Consumer)
     * @see Postgres#jsonbBuildObject(String, SymbolPeriod, TableMeta)
     */
    @Test//(invocationCount = 1000)
    public void jsonBuildObjectFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonBuildObject()::as, "json1")
                .comma(jsonBuildObject(c -> c.space("name", SQLs.literalValue("zoro"))
                                .comma("region", ChinaRegion_.name)
                                .comma("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .comma("derivedFields", space("b", PERIOD, ASTERISK))
                                .comma("regionFields", jsonBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json3")
                ).comma(jsonBuildObject(SPACE, c -> c.accept("name", SQLs.literalValue("zoro"))
                                .accept("region", ChinaRegion_.name)
                                .accept("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .accept("derivedFields", space("b", PERIOD, ASTERISK))
                                .accept("regionFields", jsonBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json4")
                ).comma(jsonbBuildObject()::as, "json5")
                .comma(jsonbBuildObject(c -> c.space("name", SQLs.literalValue("zoro"))
                                .comma("region", ChinaRegion_.name)
                                .comma("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .comma("derivedFields", space("b", PERIOD, ASTERISK))
                                .comma("regionFields", jsonbBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json7")
                ).comma(jsonbBuildObject(SPACE, c -> c.accept("name", SQLs.literalValue("zoro"))
                                .accept("region", ChinaRegion_.name)
                                .accept("row", row(SQLs.literalValue(1), SQLs.literalValue("happy")))
                                .accept("derivedFields", space("b", PERIOD, ASTERISK))
                                .accept("regionFields", jsonbBuildObject("c", PERIOD, ChinaRegion_.T))
                        ).as("json8")
                )
                .from(ChinaRegion_.T, AS, "c")
                .join(Postgres.subQuery()
                        .select(SQLs.literalValue(5)::as, "id")
                        ::asQuery
                ).as("b").on(SQLs.refThis("b", "id")::equal, ChinaRegion_.id)
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonObject(Expression)
     * @see Postgres#jsonObject(BiFunction, Consumer)
     * @see Postgres#jsonObject(SymbolSpace, BiFunction, Consumer)
     * @see Postgres#jsonbObject(Expression)
     * @see Postgres#jsonbObject(BiFunction, Consumer)
     * @see Postgres#jsonbObject(SymbolSpace, BiFunction, Consumer)
     */
    @Test
    public void jsonObjectFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonObject(SQLs.literal(TextArrayType.LINEAR, Arrays.asList("name", "zoro")))::as, "json1")
                .comma(jsonObject(SQLs::literal, c -> c.space("name", "zoro")
                                .comma("region", "china")
                                .comma("hobby", "drinking")
                        )::as, "json2"
                ).comma(jsonObject(SPACE, SQLs::literal, c -> c.accept("name", "zoro")
                                .accept("region", "china")
                                .accept("hobby", "drinking")
                        )::as, "json3"
                ).comma(jsonbObject(SQLs.literal(TextArrayType.LINEAR, Arrays.asList("name", "zoro")))::as, "json4")
                .comma(jsonbObject(SQLs::literal, c -> c.space("name", "zoro")
                                .comma("region", "china")
                                .comma("hobby", "drinking")
                        )::as, "json5"
                ).comma(jsonbObject(SPACE, SQLs::literal, c -> c.accept("name", "zoro")
                                .accept("region", "china")
                                .accept("hobby", "drinking")
                        )::as, "json6"
                )
                .asQuery();

        printStmt(LOG, stmt);

    }


    /**
     * @see Postgres#jsonArrayElements(Expression)
     */
    @Test
    public void jsonArrayElementsFuncScalar() {
        final String json;
        json = "[1,true, [2,false]]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonArrayElements(SQLs::literal, json)::as, "json1")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonbArrayElements(Expression)
     */
    @Test
    public void jsonbArrayElementsFuncScalar() {
        final String json;
        json = "[1,true, [2,false]]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbArrayElements(SQLs::literal, json)::as, "json1")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonArrayElements(Expression)
     * @see Postgres#jsonbArrayElements(Expression)
     */
    @Test
    public void jsonArrayElementsFunc() {
        final String json;
        json = "[1,true, [2,false]]";
        final Select stmt;
        stmt = Postgres.query()
                .selectDistinctOn(SQLs.refSelection("ordinal"))
                .space("json", PERIOD, ASTERISK)
                .comma("jsonOrdinal", PERIOD, ASTERISK)
                .from(jsonArrayElements(SQLs::literal, json)).as("json").parens("value")
                .crossJoin(jsonArrayElements(SQLs::literal, json).withOrdinality()).as("jsonOrdinal").parens("value", "ordinal")
                .crossJoin(jsonbArrayElements(SQLs::literal, json)).as("jsonb").parens("value")
                .crossJoin(jsonbArrayElements(SQLs::literal, json).withOrdinality()).as("jsonbOrdinal").parens("value", "ordinal")
                .asQuery();

        printStmt(LOG, stmt);

    }


    /**
     * @see Postgres#jsonArrayElementsText(Expression)
     * @see Postgres#jsonArrayElementsText(BiFunction, Object)
     */
    @Test
    public void jsonArrayElementsTextFuncScalar() {
        final String json;
        json = "[\"foo\", \"bar\"]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonArrayElementsText(SQLs::literal, json)::as, "text1")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonbArrayElementsText(Expression)
     * @see Postgres#jsonbArrayElementsText(BiFunction, Object)
     */
    @Test
    public void jsonbArrayElementsTextFuncScalar() {
        final String json;
        json = "[\"foo\", \"bar\"]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbArrayElementsText(SQLs::literal, json)::as, "json1")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonArrayElementsText(Expression)
     * @see Postgres#jsonbArrayElementsText(Expression)
     */
    @Test
    public void jsonArrayElementsTextFunc() {
        final String json;
        json = "[\"foo\", \"bar\"]";
        final Select stmt;
        stmt = Postgres.query()
                .selectDistinctOn(SQLs.refSelection("ordinal"))
                .space("json", PERIOD, ASTERISK)
                .comma("jsonOrdinal", PERIOD, ASTERISK)
                .from(jsonArrayElementsText(SQLs::literal, json)).as("json").parens("value")
                .crossJoin(jsonArrayElementsText(SQLs::literal, json).withOrdinality()).as("jsonOrdinal").parens("value", "ordinal")
                .crossJoin(jsonbArrayElementsText(SQLs::literal, json)).as("jsonb").parens("value")
                .crossJoin(jsonbArrayElementsText(SQLs::literal, json).withOrdinality()).as("jsonbOrdinal").parens("value", "ordinal")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonArrayLength(Expression)
     * @see Postgres#jsonbArrayLength(Expression)
     */
    @Test
    public void jsonArrayLengthFunc() {
        final String json;
        json = "[1,2,3,{\"f1\":1,\"f2\":[5,6]},4]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonArrayLength(SQLs.literal(JsonType.TEXT, json))::as, "json1")
                .comma(jsonbArrayLength(SQLs.literal(JsonbType.TEXT, json))::as, "json2")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonEach(Expression)
     * @see Postgres#jsonEach(BiFunction, Object)
     * @see Postgres#jsonbEach(Expression)
     * @see Postgres#jsonbEach(BiFunction, Object)
     */
    @Test
    public void jsonEachFunc() {
        final String json;
        json = "{\"a\":\"foo\", \"b\":\"bar\"}";
        final Select stmt;
        stmt = Postgres.query()
                .select("json", PERIOD, ASTERISK)
                .comma("jsonOrdinal", PERIOD, ASTERISK)
                .comma("jsonb", PERIOD, ASTERISK)
                .comma("jsonbOrdinal", PERIOD, ASTERISK)
                .from(jsonEach(SQLs::literal, json)).as("json")
                .crossJoin(jsonEach(SQLs::literal, json).withOrdinality()).as("jsonOrdinal")
                .crossJoin(jsonbEach(SQLs::literal, json)).as("jsonb")
                .crossJoin(jsonbEach(SQLs::literal, json).withOrdinality()).as("jsonbOrdinal")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonEachText(Expression)
     * @see Postgres#jsonEachText(BiFunction, Object)
     * @see Postgres#jsonbEachText(Expression)
     * @see Postgres#jsonbEachText(BiFunction, Object)
     */
    @Test
    public void jsonEachTextFunc() {
        final String json;
        json = "{\"a\":\"foo\", \"b\":\"bar\"}";
        final Select stmt;
        stmt = Postgres.query()
                .select("json", PERIOD, ASTERISK)
                .comma("jsonOrdinal", PERIOD, ASTERISK)
                .comma("jsonb", PERIOD, ASTERISK)
                .comma("jsonbOrdinal", PERIOD, ASTERISK)
                .from(jsonEachText(SQLs::literal, json)).as("json")
                .crossJoin(jsonEachText(SQLs::literal, json).withOrdinality()).as("jsonOrdinal")
                .crossJoin(jsonbEachText(SQLs::literal, json)).as("jsonb")
                .crossJoin(jsonbEachText(SQLs::literal, json)::withOrdinality).as("jsonbOrdinal")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonExtractPath(Expression, Expression, Expression...)
     * @see Postgres#jsonExtractPath(Expression, BiFunction, String, String...)
     * @see Postgres#jsonExtractPath(Expression, Consumer)
     * @see Postgres#jsonExtractPath(Expression, BiFunction, Consumer)
     * @see Postgres#jsonExtractPath(BiFunction, Object, BiFunction, String, String...)
     * @see Postgres#jsonbExtractPath(Expression, Expression, Expression...)
     * @see Postgres#jsonbExtractPath(Expression, BiFunction, String, String...)
     * @see Postgres#jsonbExtractPath(Expression, Consumer)
     * @see Postgres#jsonbExtractPath(Expression, BiFunction, Consumer)
     * @see Postgres#jsonbExtractPath(BiFunction, Object, BiFunction, String, String...)
     */
    @Test
    public void jsonExtractPathFunc() {
        final String json;
        json = "{\"f2\":{\"f3\":1},\"f4\":{\"f5\":99,\"f6\":\"foo\"}}";

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonExtractPath(SQLs.literal(JsonType.TEXT, json), SQLs.literal(NoCastTextType.INSTANCE, "f4"), SQLs.literal(NoCastTextType.INSTANCE, "f6"))::as, "json1")
                // .comma(jsonExtractPath(SQLs.literal(JsonType.TEXT, json), SQLs::multiLiteral, "f4", "f6")::as, "json2")  //TODO
                .comma(jsonExtractPath(SQLs.literal(JsonType.TEXT, json), c -> {
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f4"));
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f6"));
                        })::as, "json3"
                )
//                .comma(jsonExtractPath(SQLs.literal(JsonType.TEXT, json), SQLs::multiLiteral, c -> {
//                            c.accept("f4");
//                            c.accept("f6");
//                        })::as, "json4"
//                )
                // .comma(jsonExtractPath(SQLs::literal, json, SQLs::multiLiteral, "f4", "f6")::as, "json5")
                .comma(jsonbExtractPath(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(NoCastTextType.INSTANCE, "f4"), SQLs.literal(NoCastTextType.INSTANCE, "f6"))::as, "json6")
                // .comma(jsonbExtractPath(SQLs.literal(JsonbType.TEXT, json), SQLs::multiLiteral, "f4", "f6")::as, "json7")
                .comma(jsonbExtractPath(SQLs.literal(JsonbType.TEXT, json), c -> {
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f4"));
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f6"));
                        })::as, "json8"
                )
                //.comma(jsonbExtractPath(SQLs.literal(JsonbType.TEXT, json), SQLs::multiLiteral, c -> {
//                            c.accept("f4");
//                            c.accept("f6");
//                        })::as, "json9"
//                )
                //.comma(jsonbExtractPath(SQLs::literal, json, SQLs::multiLiteral, "f4", "f6")::as, "json10")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonExtractPathText(Expression, Expression, Expression...)
     * @see Postgres#jsonExtractPathText(Expression, BiFunction, String, String...)
     * @see Postgres#jsonExtractPathText(Expression, Consumer)
     * @see Postgres#jsonExtractPathText(Expression, BiFunction, Consumer)
     * @see Postgres#jsonExtractPathText(BiFunction, Object, BiFunction, String, String...)
     * @see Postgres#jsonbExtractPathText(Expression, Expression, Expression...)
     * @see Postgres#jsonbExtractPathText(Expression, BiFunction, String, String...)
     * @see Postgres#jsonbExtractPathText(Expression, Consumer)
     * @see Postgres#jsonbExtractPathText(Expression, BiFunction, Consumer)
     * @see Postgres#jsonbExtractPathText(BiFunction, Object, BiFunction, String, String...)
     */
    @Test
    public void jsonExtractPathTextFunc() {
        final String json;
        json = "{\"f2\":{\"f3\":1},\"f4\":{\"f5\":99,\"f6\":\"foo\"}}";

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonExtractPathText(SQLs.literal(JsonType.TEXT, json), SQLs.literal(NoCastTextType.INSTANCE, "f4"), SQLs.literal(NoCastTextType.INSTANCE, "f6"))::as, "json1")
                // .comma(jsonExtractPathText(SQLs.literal(JsonType.TEXT, json), SQLs::multiLiteral, "f4", "f6")::as, "json2")
                .comma(jsonExtractPathText(SQLs.literal(JsonType.TEXT, json), c -> {
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f4"));
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f6"));
                        })::as, "json3"
                )
//                .comma(jsonExtractPathText(SQLs.literal(JsonType.TEXT, json), SQLs::multiLiteral, c -> {
//                            c.accept("f4");
//                            c.accept("f6");
//                        })::as, "json4"
//                ).comma(jsonExtractPathText(SQLs::literal, json, SQLs::multiLiteral, "f4", "f6")::as, "json5")

                // .comma(jsonbExtractPathText(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(NoCastTextType.INSTANCE, "f4"), SQLs.literal(NoCastTextType.INSTANCE, "f6"))::as, "json6")
                //   .comma(jsonbExtractPathText(SQLs.literal(JsonbType.TEXT, json), SQLs::multiLiteral, "f4", "f6")::as, "json7")
                .comma(jsonbExtractPathText(SQLs.literal(JsonbType.TEXT, json), c -> {
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f4"));
                            c.accept(SQLs.literal(NoCastTextType.INSTANCE, "f6"));
                        })::as, "json8"
                )
//                .comma(jsonbExtractPathText(SQLs.literal(JsonbType.TEXT, json), SQLs::multiLiteral, c -> {
//                            c.accept("f4");
//                            c.accept("f6");
//                        })::as, "json9"
//                ).comma(jsonbExtractPathText(SQLs::literal, json, SQLs::multiLiteral, "f4", "f6")::as, "json10")
                .asQuery();

        printStmt(LOG, stmt);
    }


    /**
     * @see Postgres#jsonObjectKeys(Expression)
     * @see Postgres#jsonObjectKeys(BiFunction, Object)
     * @see Postgres#jsonbObjectKeys(Expression)
     * @see Postgres#jsonbObjectKeys(BiFunction, Object)
     */
    @Test
    public void jsonObjectKeysFunc() {
        final String json;
        json = "{\"f1\":\"abc\",\"f2\":{\"f3\":\"a\", \"f4\":\"b\"}}";

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonObjectKeys(SQLs.literal(JsonType.TEXT, json))::as, "json1")
                .comma(jsonObjectKeys(SQLs::literal, json)::as, "json2")
                .comma(jsonbObjectKeys(SQLs.literal(JsonbType.TEXT, json))::as, "json3")
                .comma(jsonbObjectKeys(SQLs::literal, json)::as, "json4")
                .comma("jt", PERIOD, ASTERISK)
                .from(jsonObjectKeys(SQLs.literal(JsonType.TEXT, json))).as("jt").parens("value")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonPopulateRecord(Expression, Expression)
     * @see Postgres#jsonbPopulateRecord(Expression, Expression)
     */
    @Test
    public void jsonPopulateRecordFunc() {
        final String json;
        json = "{\"a\": 1, \"b\": [\"2\", \"a b\"], \"c\": {\"d\": 4, \"e\": \"a b c\"}, \"x\": \"foo\"}";

        final Select stmt;
        stmt = Postgres.query()
                .select("d", PERIOD, ASTERISK)
                .comma("w", PERIOD, ASTERISK)
                .from(jsonPopulateRecord(SQLs.literal(MyRowType.INSTANCE, null), SQLs.literal(JsonType.TEXT, json)))
                .as("d")
                .crossJoin(jsonPopulateRecord(SQLs.literal(MyRowType.INSTANCE, null), SQLs.literal(JsonType.TEXT, json))::withOrdinality)
                .as("w")
                .crossJoin(jsonbPopulateRecord(SQLs.literal(MyRowType.INSTANCE, null), SQLs.literal(JsonbType.TEXT, json)))
                .as("db")
                .crossJoin(jsonbPopulateRecord(SQLs.literal(MyRowType.INSTANCE, null), SQLs.literal(JsonbType.TEXT, json))::withOrdinality)
                .as("wb")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonPopulateRecordSet(Expression, Expression)
     * @see Postgres#jsonbPopulateRecordSet(Expression, Expression)
     */
    @Test
    public void jsonPopulateRecordSetFunc() {
        final String json;
        json = "[{\"a\":1,\"b\":2}, {\"a\":3,\"b\":4}]";

        final Select stmt;
        stmt = Postgres.query()
                .select("d", PERIOD, ASTERISK)
                .comma("w", PERIOD, ASTERISK)
                .from(jsonPopulateRecordSet(SQLs.literal(TwoIntType.INSTANCE, null), SQLs.literal(JsonType.TEXT, json)))
                .as("d")
                .crossJoin(jsonPopulateRecordSet(SQLs.literal(TwoIntType.INSTANCE, null), SQLs.literal(JsonType.TEXT, json))::withOrdinality)
                .as("w")
                .crossJoin(jsonbPopulateRecordSet(SQLs.literal(TwoIntType.INSTANCE, null), SQLs.literal(JsonbType.TEXT, json)))
                .as("db")
                .crossJoin(jsonbPopulateRecordSet(SQLs.literal(TwoIntType.INSTANCE, null), SQLs.literal(JsonbType.TEXT, json))::withOrdinality)
                .as("wb")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonToRecord(Expression)
     * @see Postgres#jsonToRecord(BiFunction, Object)
     * @see Postgres#jsonbToRecord(Expression)
     * @see Postgres#jsonbToRecord(BiFunction, Object)
     */
    @Test//(invocationCount = 10000)
    public void jsonToRecordFunc() {
        final String json;
        json = "{\"a\":1,\"b\":[1,2,3],\"c\":[1,2,3],\"e\":\"bar\",\"r\": {\"d\": 123, \"e\": \"a b c\"}}";

        final Select stmt;
        stmt = Postgres.query()
                .select("json1", PERIOD, ASTERISK)
                .comma("json2", PERIOD, ASTERISK)
                .comma("jsonb1", PERIOD, ASTERISK)
                .comma("jsonb2", PERIOD, ASTERISK)
                .from(jsonToRecord(SQLs.literal(JsonType.TEXT, json)))
                .as("json1").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                        .comma("c", IntegerArrayType.LINEAR)
                        .comma("d", TextType.INSTANCE)
                        .comma("r", MySubRowType.INSTANCE)
                ).crossJoin(jsonToRecord(SQLs::literal, json))
                .as("json2").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                        .comma("c", IntegerArrayType.LINEAR)
                        .comma("d", TextType.INSTANCE)
                        .comma("r", MySubRowType.INSTANCE)
                ).crossJoin(jsonbToRecord(SQLs.literal(JsonbType.TEXT, json)))
                .as("jsonb1").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                        .comma("c", IntegerArrayType.LINEAR)
                        .comma("d", TextType.INSTANCE)
                        .comma("r", MySubRowType.INSTANCE)
                ).crossJoin(jsonbToRecord(SQLs::literal, json))
                .as("jsonb2").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                        .comma("c", IntegerArrayType.LINEAR)
                        .comma("d", TextType.INSTANCE)
                        .comma("r", MySubRowType.INSTANCE)
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonToRecordSet(Expression)
     * @see Postgres#jsonToRecordSet(BiFunction, Object)
     * @see Postgres#jsonbToRecordSet(Expression)
     * @see Postgres#jsonbToRecordSet(BiFunction, Object)
     */
    @Test
    public void jsonToRecordSetFunc() {
        final String json;
        json = "[{\"a\":1,\"b\":\"foo\"}, {\"a\":\"2\",\"c\":\"bar\"}]";

        final Select stmt;
        stmt = Postgres.query()
                .select("json1", PERIOD, ASTERISK)
                .comma("json2", PERIOD, ASTERISK)
                .comma("jsonb1", PERIOD, ASTERISK)
                .comma("jsonb2", PERIOD, ASTERISK)
                .from(jsonToRecordSet(SQLs.literal(JsonType.TEXT, json)))
                .as("json1").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                ).crossJoin(jsonToRecordSet(SQLs::literal, json))
                .as("json2").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                ).crossJoin(jsonbToRecordSet(SQLs.literal(JsonbType.TEXT, json)))
                .as("jsonb1").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                ).crossJoin(jsonbToRecordSet(SQLs::literal, json))
                .as("jsonb2").parens(c -> c.space("a", IntegerType.INSTANCE)
                        .comma("b", TextType.INSTANCE)
                )
                .asQuery();

        printStmt(LOG, stmt);
    }


    /**
     * @see Postgres#jsonbPathExists(Expression, Expression)
     * @see Postgres#jsonbPathExists(Expression, Expression, Expression)
     * @see Postgres#jsonbPathExists(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathExists(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathExists(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathExists(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathExists(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathExistsFunc() {
        final String json, path, varPath, vars;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "$.a[*] ? (@ >= 2 && @ <= 4)";
        varPath = "$.a[*] ? (@ >= $min && @ <= $max)";
        vars = "{\"min\":2, \"max\":4}";

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, path)::as, "json2")
                .comma(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathExists(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathMatch(Expression, Expression)
     * @see Postgres#jsonbPathMatch(Expression, Expression, Expression)
     * @see Postgres#jsonbPathMatch(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathMatch(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathMatch(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathMatch(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathMatch(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathMatchFunc() {
        final String json, path, varPath, vars;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "exists($.a[*] ? (@ >= 2 && @ <= 4))";
        varPath = "exists($.a[*] ? (@ >= $min && @ <= $max))";
        vars = "{\"min\":2, \"max\":4}";

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, path)::as, "json2")
                .comma(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathMatch(SQLs.literal(JsonbType.TEXT, json), SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathQuery(Expression, Expression)
     * @see Postgres#jsonbPathQuery(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQuery(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQuery(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQuery(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQuery(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQuery(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test//(invocationCount = 1000)
    public void jsonbPathQueryFunc() {
        final String json, path, varPath, vars;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "$.a[*] ? (@ >= 2 && @ <= 4)";
        varPath = "$.a[*] ? (@ >= $min && @ <= $max)";
        vars = "{\"min\":2, \"max\":4}";

        final Expression jsonField, pathExp, varPathExp, varExp;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        pathExp = SQLs.literal(JsonPathType.INSTANCE, path);
        varPathExp = SQLs.literal(JsonPathType.INSTANCE, varPath);
        varExp = SQLs.literal(JsonbType.TEXT, vars);

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQuery(jsonField, pathExp)::as, "json1")
                .comma(jsonbPathQuery(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQuery(jsonField, varPathExp, varExp)::as, "json3")
                .comma(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQuery(jsonField, varPathExp, varExp, TRUE)::as, "json5")
                .comma(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .comma("jt1", PERIOD, ASTERISK)
                .comma("jt2", PERIOD, ASTERISK)
                .comma("jt3", PERIOD, ASTERISK)
                .comma("jt4", PERIOD, ASTERISK)
                .comma("jt5", PERIOD, ASTERISK)
                .comma("jt6", PERIOD, ASTERISK)
                .comma("jt7", PERIOD, ASTERISK)
                .comma("jt8", PERIOD, ASTERISK)
                .comma("jt9", PERIOD, ASTERISK)
                .comma("jt10", PERIOD, ASTERISK)
                .comma("jt11", PERIOD, ASTERISK)
                .comma("jt12", PERIOD, ASTERISK)

                .from(jsonbPathQuery(jsonField, pathExp)).as("jt1").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, pathExp)::withOrdinality).as("jt2").parens("value", "ordinal")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, path)).as("jt3").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, path)::withOrdinality).as("jt4").parens("value", "ordinal")
                .crossJoin(jsonbPathQuery(jsonField, varPathExp, varExp)).as("jt5").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, varPathExp, varExp)::withOrdinality).as("jt6").parens("value", "ordinal")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars)).as("jt7").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::withOrdinality).as("jt8").parens("value", "ordinal")
                .crossJoin(jsonbPathQuery(jsonField, varPathExp, varExp, TRUE)).as("jt9").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, varPathExp, varExp, TRUE)::withOrdinality).as("jt10").parens("value", "ordinal")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)).as("jt11").parens("value")
                .crossJoin(jsonbPathQuery(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::withOrdinality).as("jt12").parens("value", "ordinal")

                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathQueryArray(Expression, Expression)
     * @see Postgres#jsonbPathQueryArray(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryArray(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryArray(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQueryArray(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQueryArray(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQueryArray(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathQueryArrayFunc() {
        final String json, path, varPath, vars;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "$.a[*] ? (@ >= 2 && @ <= 4)";
        varPath = "$.a[*] ? (@ >= $min && @ <= $max)";
        vars = "{\"min\":2, \"max\":4}";

        final Expression jsonField, pathExp, varPathExp, varExp;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        pathExp = SQLs.literal(JsonPathType.INSTANCE, path);
        varPathExp = SQLs.literal(JsonPathType.INSTANCE, varPath);
        varExp = SQLs.literal(JsonbType.TEXT, vars);

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQueryArray(jsonField, pathExp)::as, "json1")
                .comma(jsonbPathQueryArray(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQueryArray(jsonField, varPathExp, varExp)::as, "json3")
                .comma(jsonbPathQueryArray(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQueryArray(jsonField, varPathExp, varExp, TRUE)::as, "json5")
                .comma(jsonbPathQueryArray(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathQueryFirst(Expression, Expression)
     * @see Postgres#jsonbPathQueryFirst(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirst(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirst(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQueryFirst(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQueryFirst(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirst(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathQueryFirstFunc() {
        final String json, path, varPath, vars;
        json = "{\"a\":[1,2,3,4,5]}";
        path = "$.a[*] ? (@ >= 2 && @ <= 4)";
        varPath = "$.a[*] ? (@ >= $min && @ <= $max)";
        vars = "{\"min\":2, \"max\":4}";

        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQueryFirst(jsonField, SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathQueryFirst(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQueryFirst(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathQueryFirst(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQueryFirst(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathQueryFirst(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonbPathExistsTz(Expression, Expression)
     * @see Postgres#jsonbPathExistsTz(Expression, Expression, Expression)
     * @see Postgres#jsonbPathExistsTz(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathExistsTz(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathExistsTz(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathExistsTz(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathExistsTz(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathExistsTzFunc() {
        final String json, path, varPath, vars;
        json = "[\"2015-08-01 12:00:00-05\"]";
        path = "$[*] ? (@.datetime() < \"2015-08-02\".datetime())";
        varPath = "$[*] ? (@.datetime() < $d.datetime())";
        vars = "{\"d\":\"2015-08-02\"}";

        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathExistsTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathExistsTz(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathExistsTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathExistsTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathExistsTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathExistsTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathMatchTz(Expression, Expression)
     * @see Postgres#jsonbPathMatchTz(Expression, Expression, Expression)
     * @see Postgres#jsonbPathMatchTz(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathMatchTz(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathMatchTz(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathMatchTz(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathMatchTz(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathMatchTzFunc() {
        final String json, path, varPath, vars;
        json = "[\"2015-08-01 12:00:00-05\"]";
        path = "exists($[*] ? (@.datetime() < \"2015-08-02\".datetime()))";
        varPath = "exists($[*] ? (@.datetime() < $d.datetime()))";
        vars = "{\"d\":\"2015-08-02\"}";

        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathMatchTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathMatchTz(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathMatchTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathMatchTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathMatchTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathMatchTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPathQueryTz(Expression, Expression)
     * @see Postgres#jsonbPathQueryTz(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryTz(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryTz(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQueryTz(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQueryTz(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQuery(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathQueryTzFunc() {
        final String json, path, varPath, vars;
        json = "{\n" +
                "  \"a\": [\n" +
                "    \"2015-08-01 12:00:00-05\",\n" +
                "    \"2015-08-02 12:00:00-05\",\n" +
                "    \"2015-08-03 12:00:00-05\",\n" +
                "    \"2015-08-04 12:00:00-05\",\n" +
                "    \"2015-08-05 12:00:00-05\"\n" +
                "  ]\n" +
                "}";
        path = "$.a[*] ? (@.datetime() < \"2015-08-05\".datetime())";
        varPath = "$.a[*] ? (@.datetime() < $d.datetime())";
        vars = "{\"d\":\"2015-08-05\"}";

        final Expression jsonField, pathExp, varPathExp, varExp;
        jsonField = SQLs.literal(JsonbType.TEXT, json);
        pathExp = SQLs.literal(JsonPathType.INSTANCE, path);
        varPathExp = SQLs.literal(JsonPathType.INSTANCE, varPath);
        varExp = SQLs.literal(JsonbType.TEXT, vars);

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQueryTz(jsonField, pathExp)::as, "json1")
                .comma(jsonbPathQueryTz(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQueryTz(jsonField, varPathExp, varExp)::as, "json3")
                .comma(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQueryTz(jsonField, varPathExp, varExp, TRUE)::as, "json5")
                .comma(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .comma("jt1", PERIOD, ASTERISK)
                .comma("jt2", PERIOD, ASTERISK)
                .comma("jt3", PERIOD, ASTERISK)
                .comma("jt4", PERIOD, ASTERISK)
                .comma("jt5", PERIOD, ASTERISK)
                .comma("jt6", PERIOD, ASTERISK)
                .comma("jt7", PERIOD, ASTERISK)
                .comma("jt8", PERIOD, ASTERISK)
                .comma("jt9", PERIOD, ASTERISK)
                .comma("jt10", PERIOD, ASTERISK)
                .comma("jt11", PERIOD, ASTERISK)
                .comma("jt12", PERIOD, ASTERISK)

                .from(jsonbPathQueryTz(jsonField, pathExp)).as("jt1").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, pathExp)::withOrdinality).as("jt2").parens("value", "ordinal")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, path)).as("jt3").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, path)::withOrdinality).as("jt4").parens("value", "ordinal")
                .crossJoin(jsonbPathQueryTz(jsonField, varPathExp, varExp)).as("jt5").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, varPathExp, varExp)::withOrdinality).as("jt6").parens("value", "ordinal")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)).as("jt7").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::withOrdinality).as("jt8").parens("value", "ordinal")
                .crossJoin(jsonbPathQueryTz(jsonField, varPathExp, varExp, TRUE)).as("jt9").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, varPathExp, varExp, TRUE)::withOrdinality).as("jt10").parens("value", "ordinal")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)).as("jt11").parens("value")
                .crossJoin(jsonbPathQueryTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::withOrdinality).as("jt12").parens("value", "ordinal")

                .asQuery();

        printStmt(LOG, stmt);
    }


    /**
     * @see Postgres#jsonbPathQueryArrayTz(Expression, Expression)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQueryArrayTz(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathQueryArrayTzFunc() {
        final String json, path, varPath, vars;
        json = "{\n" +
                "  \"a\": [\n" +
                "    \"2015-08-01 12:00:00-05\",\n" +
                "    \"2015-08-02 12:00:00-05\",\n" +
                "    \"2015-08-03 12:00:00-05\",\n" +
                "    \"2015-08-04 12:00:00-05\",\n" +
                "    \"2015-08-05 12:00:00-05\"\n" +
                "  ]\n" +
                "}";
        path = "$.a[*] ? (@.datetime() < \"2015-08-05\".datetime())";
        varPath = "$.a[*] ? (@.datetime() < $d.datetime())";
        vars = "{\"d\":\"2015-08-05\"}";
        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQueryArrayTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathQueryArrayTz(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQueryArrayTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathQueryArrayTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQueryArrayTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathQueryArrayTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }


    /**
     * @see Postgres#jsonbPathQueryFirstTz(Expression, Expression)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, Expression, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, BiFunction, Object)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, BiFunction, Object, Expression)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, BiFunction, Object, Expression, Expression)
     * @see Postgres#jsonbPathQueryFirstTz(Expression, BiFunction, Object, BiFunction, Object, Expression)
     */
    @Test
    public void jsonbPathQueryFirstTzFunc() {
        final String json, path, varPath, vars;
        json = "{\n" +
                "  \"a\": [\n" +
                "    \"2015-08-01 12:00:00-05\",\n" +
                "    \"2015-08-02 12:00:00-05\",\n" +
                "    \"2015-08-03 12:00:00-05\",\n" +
                "    \"2015-08-04 12:00:00-05\",\n" +
                "    \"2015-08-05 12:00:00-05\"\n" +
                "  ]\n" +
                "}";
        path = "$.a[*] ? (@.datetime() < \"2015-08-05\".datetime())";
        varPath = "$.a[*] ? (@.datetime() < $d.datetime())";
        vars = "{\"d\":\"2015-08-05\"}";
        final Expression jsonField;
        jsonField = SQLs.literal(JsonbType.TEXT, json);

        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPathQueryFirstTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, path))::as, "json1")
                .comma(jsonbPathQueryFirstTz(jsonField, SQLs::literal, path)::as, "json2")
                .comma(jsonbPathQueryFirstTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars))::as, "json3")
                .comma(jsonbPathQueryFirstTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars)::as, "json4")
                .comma(jsonbPathQueryFirstTz(jsonField, SQLs.literal(JsonPathType.INSTANCE, varPath), SQLs.literal(JsonbType.TEXT, vars), TRUE)::as, "json5")
                .comma(jsonbPathQueryFirstTz(jsonField, SQLs::literal, varPath, SQLs::literal, vars, TRUE)::as, "json6")
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#jsonbPretty(Expression)
     */
    @Test
    public void jsonbPrettyFunc() {
        final String json;
        json = "[{\"f1\":1,\"f2\":null}, 2]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbPretty(SQLs.literal(JsonbType.TEXT, json))::as, "json")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonTypeOf(Expression)
     */
    @Test
    public void jsonTypeOfFunc() {
        final String json;
        json = "[{\"f1\":1,\"f2\":null}, 2]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonTypeOf(SQLs.literal(JsonType.TEXT, json))::as, "json")
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#jsonbTypeOf(Expression)
     */
    @Test
    public void jsonbTypeOfFunc() {
        final String json;
        json = "[{\"f1\":1,\"f2\":null}, 2]";
        final Select stmt;
        stmt = Postgres.query()
                .select(jsonbTypeOf(SQLs.literal(JsonbType.TEXT, json))::as, "json")
                .asQuery();

        printStmt(LOG, stmt);

    }


}
