package io.army.criteria;

import com.example.domain.account.Account;
import com.example.domain.account.Account_;
import com.example.domain.account.BalanceAccount;
import com.example.domain.account.BalanceAccount_;
import io.army.criteria.impl.SQLS;
import io.army.meta.FieldMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class InsertTests {

    private static final Logger LOG = LoggerFactory.getLogger(InsertTests.class);

    @Test
    public void singleInsert() {
        List<FieldMeta<? super BalanceAccount, ?>> fieldMetaList = new ArrayList<>();
        fieldMetaList.add(BalanceAccount_.balance);

        Insert insert = SQLS.insert(BalanceAccount_.T)
                .insertInto(fieldMetaList)
                .value(new BalanceAccount())
                .asInsert();
    }

    @Test
    public void multiInsert() {
        List<FieldMeta<? super BalanceAccount, ?>> fieldMetaList = new ArrayList<>();
        fieldMetaList.add(BalanceAccount_.createTime);

        List<BalanceAccount> valueList = new ArrayList<>();

        Insert insert = SQLS.insert(BalanceAccount_.T)
                .insertInto(fieldMetaList)
                .values(valueList)
                .asInsert();

    }

    @Test
    public void batchInsert() {
        List<BalanceAccount> valueList = new ArrayList<>();

        /*Insert insert = SQLS.(BalanceAccount_.T)
                .commonValue(BalanceAccount_.balance, SQLS.constant(new BigDecimal(4)).add(new BigDecimal(4)))
                .insert
                .asInsert();*/
    }

    @Test
    public void subQueryInsert() {
        List<FieldMeta<Account, ?>> fieldMetaList = new ArrayList<>();
        fieldMetaList.add(Account_.balance);
        Insert insert = SQLS.subQueryInsert(Account_.T)
                .insertInto(fieldMetaList)
                .values(this::valuesSubQuery)
                .asInsert();
    }

    private SubQuery valuesSubQuery(EmptyObject emptyObject) {
        return null;
    }
}
