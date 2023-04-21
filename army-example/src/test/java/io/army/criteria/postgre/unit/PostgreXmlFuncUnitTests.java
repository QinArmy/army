package io.army.criteria.postgre.unit;

import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.impl.Postgres;
import io.army.criteria.impl.SQLs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;

import static io.army.criteria.impl.Postgres.*;
import static io.army.criteria.impl.SQLs.AS;

public class PostgreXmlFuncUnitTests extends PostgreUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(PostgreXmlFuncUnitTests.class);

    /**
     * @see Postgres#xmlElement(Postgres.WordName, String, Postgres.XmlAttributes, Expression...)
     * @see Postgres#xmlElement(Postgres.WordName, String, Expression...)
     * @see Postgres#xmlElement(Postgres.WordName, String, List)
     * @see Postgres#xmlElement(Postgres.WordName, String, Postgres.XmlAttributes, List)
     */
    @Test
    public void xmlElement1() {
        final Select stmt;
        stmt = Postgres.query()
                .select(xmlElement(NAME, "foo",
                                xmlAttributes(c -> {
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


}
