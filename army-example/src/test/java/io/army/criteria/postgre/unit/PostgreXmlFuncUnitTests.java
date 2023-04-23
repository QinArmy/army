package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import io.army.mapping.FloatType;
import io.army.mapping.IntegerType;
import io.army.mapping.TextType;
import io.army.mapping.XmlType;
import io.army.mapping.optional.TextArrayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.Consumer;

import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.*;

public class PostgreXmlFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreXmlFuncUnitTests.class);

    /**
     * @see Postgres#xmlElement(WordName, String, XmlAttributes, Expression...)
     * @see Postgres#xmlElement(WordName, String, Expression...)
     * @see Postgres#xmlElement(WordName, String, List)
     * @see Postgres#xmlElement(WordName, String, XmlAttributes, List)
     */
    @Test
    public void xmlElementFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(Postgres.xmlElement(NAME, "foo",
                                        Postgres.xmlAttributes(c -> {
                                            c.accept(SQLs::literal, "zoro", AS, "name");
                                            c.accept(SQLs::literal, "drinking", AS, "hobby");
                                        }),
                                        SQLs.literalValue("zoro is better swordman"),
                                        SQLs.literalValue(",and he love drinking.")
                                )
                                .as("xml")
                ).asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#xmlPi(WordName, String)
     * @see Postgres#xmlPi(WordName, String, Expression)
     */
    @Test
    public void xmlPiFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(Postgres.xmlPi(NAME, "java", SQLs::literal, "echo \"hello world\";")::as, "xml1")
                .comma(Postgres.xmlPi(NAME, "java")::as, "xml2")
                .asQuery();
        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#xmlAgg(Expression)
     * @see Postgres#xmlAgg(Expression, Consumer)
     */
    @Test
    public void xmlAggFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(Postgres.xmlAgg(Postgres.xmlAgg(SQLs.literal(XmlType.TEXT_INSTANCE, "<foo>abc</foo>")),
                        c -> c.orderBy(SQLs.literalValue(1))).as("xmlagg")
                )
                .asQuery();
        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#xmlExists(Expression, WordPassing, Expression)
     * @see Postgres#xmlExists(Expression, WordPassing, PassingOption, Expression)
     * @see Postgres#xmlExists(Expression, WordPassing, Expression, PassingOption)
     * @see Postgres#xmlExists(Expression, WordPassing, PassingOption, Expression, PassingOption)
     */
    @Test
    public void xmlExistsFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(Postgres.xmlExists(SQLs.literalValue("//town[text() = 'Toronto']"), PASSING, BY_VALUE,
                        SQLs.literal(XmlType.TEXT_INSTANCE, "<towns><town>Toronto</town><town>Ottawa</town></towns>"), BY_VALUE)::as, "match"
                )
                .asQuery();
        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#xpath(Expression, Expression)
     * @see Postgres#xpath(Expression, Expression, Expression)
     */
    @Test
    public void xpathFunc() {
        final Select stmt;
        stmt = Postgres.query()
                .select(xpath(SQLs.literalValue("/my:a/text()"),
                                SQLs.literal(XmlType.TEXT_INSTANCE, "<my:a xmlns:my=\"http://example.com\">test</my:a>"),
                                SQLs.literal(TextArrayType.LINEAR, "{{my,http://example.com}}")
                        ).as("xpath")
                ).asQuery();

        printStmt(LOG, stmt);
    }

    /**
     * @see Postgres#xmlTable(XmlNameSpaces, Expression, WordPassing, Expression, Consumer)
     * @see Postgres#xmlTable(Expression, WordPassing, Expression, PassingOption, Consumer)
     * @see <a href="https://www.postgresql.org/docs/current/functions-xml.html#FUNCTIONS-XML-PROCESSING">XMLTABLE</a>
     */
    @Test
    public void xmlTableFunc() {
        final String xmlDoc;
        xmlDoc = "<ROWS>\n" +
                "  <ROW id=\"1\">\n" +
                "    <COUNTRY_ID>AU</COUNTRY_ID>\n" +
                "    <COUNTRY_NAME>Australia</COUNTRY_NAME>\n" +
                "  </ROW>\n" +
                "  <ROW id=\"5\">\n" +
                "    <COUNTRY_ID>JP</COUNTRY_ID>\n" +
                "    <COUNTRY_NAME>Japan</COUNTRY_NAME>\n" +
                "    <PREMIER_NAME>Shinzo Abe</PREMIER_NAME>\n" +
                "    <SIZE unit=\"sq_mi\">145935</SIZE>\n" +
                "  </ROW>\n" +
                "  <ROW id=\"6\">\n" +
                "    <COUNTRY_ID>SG</COUNTRY_ID>\n" +
                "    <COUNTRY_NAME>Singapore</COUNTRY_NAME>\n" +
                "    <SIZE unit=\"sq_km\">697</SIZE>\n" +
                "  </ROW>\n" +
                "</ROWS>";

        final Select stmt;
        stmt = Postgres.query()
                .select("x", PERIOD, ASTERISK)
                .from(() -> xmlTable(xmlNamespaces(SQLs::literal, "http://example.com/myns", AS, "xz"),
                                SQLs.literal(TextType.INSTANCE, "//ROWS/ROW"), PASSING, BY_VALUE, SQLs.literal(XmlType.TEXT_INSTANCE, xmlDoc), BY_VALUE,
                                c -> c.columns("id", IntegerType.INSTANCE, PATH, SQLs.literal(TextType.INSTANCE, "@id"))
                                        .comma("ordinality", FOR_ORDINALITY)
                                        .comma("COUNTRY_NAME", TextType.INSTANCE)
                                        .comma("country_id", TextType.INSTANCE, PATH, SQLs.literal(TextType.INSTANCE, "COUNTRY_ID"))
                                        .comma("size_sq_km", FloatType.INSTANCE, PATH, SQLs.literal(TextType.INSTANCE, "SIZE[@unit = \"sq_km\"]"))
                                        .comma("size_other", TextType.INSTANCE, PATH, SQLs.literal(TextType.INSTANCE, "concat(SIZE[@unit!=\"sq_km\"], \" \", SIZE[@unit!=\"sq_km\"]/@unit)"))
                                        .comma("premier_name", TextType.INSTANCE, PATH, SQLs.literal(TextType.INSTANCE, "PREMIER_NAME"), DEFAULT, SQLs.literal(TextType.INSTANCE, "not specified"))
                        )//xmlTable
                ).as("x")
                .asQuery();

        printStmt(LOG, stmt);
    }


}
