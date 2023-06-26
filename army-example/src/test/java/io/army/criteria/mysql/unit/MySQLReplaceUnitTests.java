package io.army.criteria.mysql.unit;

import io.army.annotation.GeneratorType;
import io.army.criteria.Insert;
import io.army.criteria.LiteralMode;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.domain.user.BankUser_;
import io.army.example.bank.domain.user.ChinaRegion_;
import io.army.util._Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class MySQLReplaceUnitTests extends MySQLUnitTests {

    private static final Logger LOG = LoggerFactory.getLogger(MySQLReplaceUnitTests.class);


    @Test
    public void domainReplace80ParentPrecede() {
        assert BankUser_.id.generatorType() == GeneratorType.PRECEDE;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = _Collections.arrayList();
            hintList.add(MySQLs.qbName("userBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.PREFERENCE)
                .replace(hintSupplier, Collections.singletonList(MySQLs.DELAYED))
                .into(BankUser_.T)
                .partition("p1")
                .parens(s -> s.space(BankUser_.nickName, BankUser_.certificateId)
                        .comma(BankUser_.registerRecordId)
                )
                .defaultValue(BankUser_.visible, SQLs::literal, true)
                .values(this::createBankUserList)
                .asInsert();

        print80Stmt(LOG, stmt);
    }

    @Test
    public void domainReplace80ParentPost() {
        assert ChinaRegion_.id.generatorType() == GeneratorType.POST;

        final Supplier<List<Hint>> hintSupplier;
        hintSupplier = () -> {
            List<Hint> hintList = _Collections.arrayList();
            hintList.add(MySQLs.qbName("regionBlock"));
            return hintList;
        };

        Insert stmt;
        stmt = MySQLs.singleReplace()
                .literalMode(LiteralMode.PREFERENCE)
                .replace(hintSupplier, Collections.singletonList(MySQLs.LOW_PRIORITY))
                .into(ChinaRegion_.T)
                .partition("p1")
                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
                        .comma(ChinaRegion_.parentId)
                )
                .defaultValue(ChinaRegion_.visible, SQLs::literal, true)
                .values(this::createReginList)
                .asInsert();

        print80Stmt(LOG, stmt);

    }


}
