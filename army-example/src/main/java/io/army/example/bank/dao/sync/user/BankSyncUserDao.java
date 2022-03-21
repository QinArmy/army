package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.ban.PersonAccountStatesBean;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.BankUser_;
import io.army.example.bank.domain.user.CertificateType;
import io.army.example.bank.domain.user.Certificate_;
import org.springframework.stereotype.Repository;

@Repository("bankSyncUserDao")
public class BankSyncUserDao extends BankSyncBaseDao implements BankUserDao {


    @Override
    public PersonAccountStatesBean getPersonAccountStates(final String partnerNo, final String certificateNo
            , final CertificateType certificateType) {
        final Select stmt;
        stmt = SQLs.query()
                .select(list -> {
                    list.add(SQLs.field("p", BankUser_.id).as("partnerUserId"));
                    list.add(SQLs.field("p", BankUser_.userType).as("partnerUserType"));

                    list.add(Certificate_.id.as("certificateId"));
                    list.add(Certificate_.certificateType);
                    list.add(Certificate_.certificateNo);

                    list.add(SQLs.field("pu", BankUser_.id).as("userId"));
                    list.add(SQLs.field("pu", BankUser_.userType));
                    list.add(BankAccount_.id.as("accountId"));
                    list.add(BankAccount_.accountType);
                })
                .from(BankUser_.T, "p")
                .leftJoin(BankUser_.T, "pu").on(SQLs.field("p", BankUser_.id).equal(SQLs.field("pu", BankUser_.partnerUserId)))
                .leftJoin(Certificate_.T, "c").on(Certificate_.id.equal(SQLs.field("pu", BankUser_.certificateId)))
                .leftJoin(BankAccount_.T, "a").on(BankAccount_.userId.equal(SQLs.field("pu", BankUser_.id)))
                .where(SQLs.field("p", BankUser_.userNo).equal("partnerNo"))
                .and(Certificate_.certificateNo.equal("certificateNo"))
                .and(Certificate_.certificateType.equalLiteral(CertificateType.PERSON))
                .asQuery();

        return this.sessionContext.currentSession().selectOne(stmt, PersonAccountStatesBean.class);
    }


}
