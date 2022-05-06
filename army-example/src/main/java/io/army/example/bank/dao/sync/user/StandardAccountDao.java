package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository("bankSyncStandardAccountDao")
@Profile({BaseService.SYNC, BeanUtils.STANDARD})
public class StandardAccountDao extends BankSyncBaseDao implements BankAccountDao {


    @Override
    public Map<String, Object> getRegisterUserInfo(final String requestNo) {

        final Select stmt;
        stmt = SQLs.query()
                .select(list -> {
                    // due to SQLs.field("u", BankUser_.userNo) need criteria context,so couldn't create selection list
                    // before SQLs.query().
                    list.add(SQLs.field("u", BankUser_.userNo));
                    list.add(SQLs.field("u", BankUser_.userType));
                    list.add(BankAccount_.accountNo);
                    list.add(BankAccount_.accountType);

                    list.add(SQLs.field("pu", BankUser_.userNo));
                    list.add(RegisterRecord_.createTime.as("requestTime"));
                    list.add(RegisterRecord_.handleTime);
                    list.add(RegisterRecord_.completionTime);

                })
                .from(RegisterRecord_.T, "r")
                .join(BankUser_.T, "pu").on(RegisterRecord_.partnerId.equal(SQLs.field("pu", BankUser_.id)))
                .join(BankUser_.T, "u").on(RegisterRecord_.userId.equal(SQLs.field("u", BankUser_.id)))
                .join(BankAccount_.T, "a").on(BankAccount_.userId.equal(SQLs.field("u", BankUser_.id)))
                .whereIf(RegisterRecord_.requestNo.equal(requestNo))
                .and(RegisterRecord_.id.equal(SQLs.field("u", BankUser_.registerRecordId)))
                .and(RegisterRecord_.id.equal(BankAccount_.registerRecordId))
                .asQuery();
        return this.sessionContext.currentSession().queryOneAsMap(stmt);
    }


    @Override
    public Map<String, Object> getPartnerAccountStatus(String requestNo, String certificateNo
            , CertificateType certificateType) {

        //due to selectionList don't depend on criteria context,so you can create selectionList before SQLs.query().
        final List<Selection> selectionList = new ArrayList<>(5);
        selectionList.add(RegisterRecord_.requestNo);
        selectionList.add(BankUser_.userNo);
        selectionList.add(BankUser_.userType);
        selectionList.add(BankAccount_.accountNo);

        selectionList.add(BankAccount_.accountType);

        final Select stmt;
        stmt = SQLs.query()
                .select(selectionList)
                .from(RegisterRecord_.T, "r")
                .join(BankUser_.T, "u").on(BankUser_.id.equal(RegisterRecord_.userId))
                .join(BankAccount_.T, "a").on(BankUser_.id.equal(BankAccount_.userId))
                .join(Certificate_.T, "c").on(Certificate_.id.equal(BankUser_.certificateId))
                .whereIf(RegisterRecord_.requestNo.equal(requestNo))
                .and(Certificate_.certificateNo.equal(certificateNo))
                .and(Certificate_.certificateType.equalLiteral(certificateType))
                .and(BankUser_.userType.equalLiteral(BankUserType.PARTNER))
                .and(BankUser_.registerRecordId.equal(RegisterRecord_.id))
                .and(BankAccount_.registerRecordId.equal(RegisterRecord_.id))
                .asQuery();
        return this.sessionContext.currentSession().queryOneAsMap(stmt);
    }


}
