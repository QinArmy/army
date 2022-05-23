package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import io.army.example.common.BaseService;
import io.army.example.common.BeanUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("bankSyncStandardAccountDao")
@Profile({BaseService.SYNC, BeanUtils.STANDARD})
public class StandardAccountDao extends BankSyncBaseDao implements BankAccountDao {


    @Override
    public Map<String, Object> getRegisterUserInfo(final String requestNo) {

        final Select stmt;
        stmt = SQLs.query()
                .select(consumer -> {
                    // due to SQLs.field("u", BankUser_.userNo) need criteria context,so couldn't create selection list
                    // before SQLs.query().
                    consumer.accept(SQLs.field("u", BankUser_.userNo));
                    consumer.accept(SQLs.field("u", BankUser_.userType));
                    consumer.accept(BankAccount_.accountNo);
                    consumer.accept(BankAccount_.accountType);

                    consumer.accept(SQLs.field("pu", BankUser_.userNo));
                    consumer.accept(RegisterRecord_.createTime.as("requestTime"));
                    consumer.accept(RegisterRecord_.handleTime);
                    consumer.accept(RegisterRecord_.completionTime);

                })
                .from(RegisterRecord_.T, "r")
                .join(BankUser_.T, "pu").on(RegisterRecord_.partnerId.equal(SQLs.field("pu", BankUser_.id)))
                .join(BankUser_.T, "u").on(RegisterRecord_.userId.equal(SQLs.field("u", BankUser_.id)))
                .join(BankAccount_.T, "a").on(BankAccount_.userId.equal(SQLs.field("u", BankUser_.id)))
                .where(RegisterRecord_.requestNo.equal(requestNo))
                .and(RegisterRecord_.id.equal(SQLs.field("u", BankUser_.registerRecordId)))
                .and(RegisterRecord_.id.equal(BankAccount_.registerRecordId))
                .asQuery();
        return this.sessionContext.currentSession().queryOneAsMap(stmt);
    }


    @Override
    public Map<String, Object> getPartnerAccountStatus(String requestNo, String certificateNo
            , CertificateType certificateType) {

        final Select stmt;
        stmt = SQLs.query()
                .select(consumer -> {
                    consumer.accept(RegisterRecord_.requestNo);
                    consumer.accept(BankUser_.userNo);
                    consumer.accept(BankUser_.userType);
                    consumer.accept(BankAccount_.accountNo);

                    consumer.accept(BankAccount_.accountType);
                })
                .from(RegisterRecord_.T, "r")
                .join(BankUser_.T, "u").on(BankUser_.id.equal(RegisterRecord_.userId))
                .join(BankAccount_.T, "a").on(BankUser_.id.equal(BankAccount_.userId))
                .join(Certificate_.T, "c").on(Certificate_.id.equal(BankUser_.certificateId))
                .where(RegisterRecord_.requestNo.equal(requestNo))
                .and(Certificate_.certificateNo.equal(certificateNo))
                .and(Certificate_.certificateType.equalLiteral(certificateType))
                .and(BankUser_.userType.equalLiteral(BankUserType.PARTNER))
                .and(BankUser_.registerRecordId.equal(RegisterRecord_.id))
                .and(BankAccount_.registerRecordId.equal(RegisterRecord_.id))
                .asQuery();
        return this.sessionContext.currentSession().queryOneAsMap(stmt);
    }


}
