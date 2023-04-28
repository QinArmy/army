package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.JsonType;
import io.army.mapping.JsonbType;
import io.army.mapping.TextType;
import io.army.mapping.optional.TextArrayType;
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
                        .apply(Postgres::hyphenGt, SQLs.literalValue(2))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText)
                        .apply(Postgres::hyphenGt, SQLs.literalValue(-3))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .apply(Postgres::hyphenGt, SQLs.literalValue(2))
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
                .select(SQLs.literal(JsonType.TEXT, jsonText).apply(Postgres::hyphenGtGt, SQLs.literalValue(2))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText).apply(Postgres::hyphenGtGt, SQLs.literalValue(-3))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .apply(Postgres::hyphenGtGt, SQLs.literalValue(2))
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
                .select(SQLs.literal(JsonType.TEXT, jsonText).apply(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText).apply(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .apply(Postgres::poundGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))
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
                        .apply(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonElement"
                )
                .comma(SQLs.literal(JsonbType.TEXT, jsonText)
                        .apply(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))::as, "jsonbElement"
                )
                .asQuery();

        printStmt(LOG, stmt);

        final TypeMeta expType;
        expType = SQLs.literal(JsonType.TEXT, jsonText)
                .apply(Postgres::poundGtGt, SQLs.literal(TextArrayType.LINEAR, "{a,b,1}"))
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
                        .test(Postgres::atGt, SQLs.literal(JsonbType.TEXT, secondJson))::as, "match"
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
                        .test(Postgres::ltAt, SQLs.literal(JsonbType.TEXT, firstJson))::as, "match"
                )
                .asQuery();

        printStmt(LOG, stmt);
    }


}
