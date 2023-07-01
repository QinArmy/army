package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.IntegerType;
import io.army.mapping.JsonType;
import io.army.mapping.JsonbType;
import io.army.mapping.TextType;
import io.army.mapping.array.TextArrayType;
import io.army.mapping.optional.JsonPathType;
import io.army.meta.TypeMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;


public class PostgreJsonOperatorUnitTests extends PostgreUnitTests {


    private static final Logger LOG = LoggerFactory.getLogger(PostgreJsonOperatorUnitTests.class);


    /**
     * @see Postgres#hyphenGt(Expression, Expression)
     */
    @Test
    public void hyphenGt() {
        final String jsonText = "[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonType.TEXT, jsonText)
                        .space(Postgres::hyphenGt, SQLs.literalValue(2))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText)
                        .space(Postgres::hyphenGt, SQLs.literalValue(-3))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .space(Postgres::hyphenGt, SQLs.literalValue(2))
                .typeMeta();
        Assert.assertTrue(expType instanceof JsonType);
    }

    /**
     * @see Postgres#hyphenGtGt(Expression, Expression)
     */
    @Test
    public void hyphenGtGt() {
        final String jsonText = "[{\"a\":\"foo\"},{\"b\":\"bar\"},{\"c\":\"baz\"}]";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonType.TEXT, jsonText).space(Postgres::hyphenGtGt, SQLs.literalValue(2))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText).space(Postgres::hyphenGtGt, SQLs.literalValue(-3))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .space(Postgres::hyphenGtGt, SQLs.literalValue(2))
                .typeMeta();
        Assert.assertTrue(expType instanceof TextType);
    }

    /**
     * @see Postgres#poundGt(Expression, Expression)
     */
    @Test
    public void poundGt() {
        final String jsonText = "{\"a\": {\"b\": [\"foo\",\"bar\"]}}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonType.TEXT, jsonText).space(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText).space(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .space(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))
                .typeMeta();
        Assert.assertTrue(expType instanceof JsonType);
    }

    /**
     * @see Postgres#poundGtGt(Expression, Expression)
     */
    @Test
    public void poundGtGt() {
        final String jsonText = "{\"a\": {\"b\": [\"foo\",\"bar\"]}}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonType.TEXT, jsonText)
                        .space(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText)
                        .space(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .space(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))
                .typeMeta();
        Assert.assertTrue(expType instanceof TextType);

    }


    /**
     * @see Postgres#atGt(Expression, Expression)
     */
    @Test
    public void atGt() {
        final String firstJson = "{\"a\":1, \"b\":2}", secondJson = "{\"b\":2}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, firstJson)
                        .whiteSpace(Postgres::atGt, SQLs.literal(JsonbType.TEXT, secondJson))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#ltAt(Expression, Expression)
     */
    @Test
    public void ltAt() {
        final String firstJson = "{\"a\":1, \"b\":2}", secondJson = "{\"b\":2}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, secondJson)
                        .whiteSpace(Postgres::ltAt, SQLs.literal(JsonbType.TEXT, firstJson))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#question(Expression, Expression)
     */
    @Test
    public void question() {
        final String jsonb = "{\"a\":1, \"b\":2, \"c\":3}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, jsonb)
                        .whiteSpace(Postgres::question, SQLs.literal(TextType.INSTANCE, "b"))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#questionVertical(Expression, Expression)
     */
    @Test
    public void questionVertical() {
        final String jsonb = "{\"a\":1, \"b\":2, \"c\":3}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, jsonb)
                        .whiteSpace(Postgres::questionVertical, SQLs.literal(TextArrayType.LINEAR, "{b,d}"))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#questionAmp(Expression, Expression)
     */
    @Test
    public void questionAmp() {
        final String jsonb = "{\"a\":1, \"b\":2, \"c\":3}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, jsonb)
                        .whiteSpace(Postgres::questionAmp, SQLs.literal(TextArrayType.LINEAR, "{a,b}"))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#doubleVertical(Expression, Expression)
     */
    @Test
    public void doubleVertical() {
        final String leftArray, rightArray, leftObject, rightObject;
        leftArray = "[\"a\", \"b\"]";
        rightArray = "[\"a\", \"d\"]";
        leftObject = "{\"a\": \"b\"}";
        rightObject = "{\"c\": \"d\"}";

        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, leftArray)
                        .space(Postgres::doubleVertical, SQLs::literal, rightArray)::as, "concatArray"
                ).comma(SQLs.literal(JsonbType.TEXT, leftObject)
                        .space(Postgres::doubleVertical, SQLs::literal, rightObject)::as, "concatObject"
                )
                .asQuery();

        printStmt(LOG, stmt);

        TypeMeta expType;
        expType = SQLs.literal(JsonbType.TEXT, leftArray)
                .space(Postgres::doubleVertical, SQLs::literal, rightArray)
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);

        expType = SQLs.literal(JsonbType.TEXT, leftObject)
                .space(Postgres::doubleVertical, SQLs::literal, rightObject)
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);

    }

    /**
     * @see Postgres#minus(Expression, Expression)
     */
    @Test
    public void minus() {
        final String leftArray, leftObject;
        leftArray = "[\"a\", \"b\", \"c\", \"b\"]";
        leftObject = "{\"a\": \"b\", \"c\": \"d\"}";

        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, leftArray)
                        .space(Postgres::minus, SQLs.literal(TextType.INSTANCE, "a"))::as, "minusArrayElement"
                ).comma(SQLs.literal(JsonbType.TEXT, leftObject)
                        .space(Postgres::minus, SQLs.literal(TextType.INSTANCE, "b"))::as, "minusObjectKey"
                ).comma(SQLs.literal(JsonbType.TEXT, leftArray)
                        .space(Postgres::minus, SQLs.literal(IntegerType.INTEGER, 1))::as, "minusNumberElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        TypeMeta expType;
        expType = SQLs.literal(JsonbType.TEXT, leftArray)
                .space(Postgres::minus, SQLs.literal(TextType.INSTANCE, "a"))
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);


        expType = SQLs.literal(JsonbType.TEXT, leftObject)
                .space(Postgres::minus, SQLs.literal(TextType.INSTANCE, "b"))
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);

        expType = SQLs.literal(JsonbType.TEXT, leftArray)
                .space(Postgres::minus, SQLs.literal(IntegerType.INTEGER, 1))
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);

    }

    /**
     * @see Postgres#poundHyphen(Expression, Expression)
     */
    @Test
    public void poundHyphen() {
        final String jsonbArray;
        jsonbArray = "[\"a\", {\"b\":1}]";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, jsonbArray)
                        .space(Postgres::poundHyphen, SQLs.literal(TextArrayType.LINEAR, "{1,b}"))::as, "minusArrayElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        TypeMeta expType;
        expType = SQLs.literal(JsonbType.TEXT, jsonbArray)
                .space(Postgres::poundHyphen, SQLs.literal(TextArrayType.LINEAR, "{1,b}"))
                .typeMeta();

        Assert.assertSame(expType, JsonbType.TEXT);
    }

    /**
     * @see Postgres#atQuestion(Expression, Expression)
     */
    @Test
    public void atQuestion() {
        final String json;
        json = "{\"a\":[1,2,3,4,5]}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, json)
                        .whiteSpace(Postgres::atQuestion, SQLs.literal(JsonPathType.INSTANCE, "$.a[*] ? (@ > 2)"))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);

    }

    /**
     * @see Postgres#doubleAt(Expression, Expression)
     */
    @Test
    public void doubleAt() {
        final String json;
        json = "{\"a\":[1,2,3,4,5]}";
        final Select stmt;
        stmt = Postgres.query()
                .select(SQLs.literal(JsonbType.TEXT, json)
                        .whiteSpace(Postgres::doubleAt, SQLs.literal(JsonPathType.INSTANCE, "$.a[*] > 2"))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);

    }


}
