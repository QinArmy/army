package io.army.example.bank.dao.sync.user;

import io.army.criteria.Select;
import io.army.criteria.Selection;
import io.army.criteria.impl.SQLs;
import io.army.example.bank.bean.PersonAccountStatesBean;
import io.army.example.bank.dao.sync.BankSyncBaseDao;
import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.account.BankAccount_;
import io.army.example.bank.domain.user.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository("bankSyncAccountDao")
public class BankSyncAccountDao extends BankSyncBaseDao implements BankAccountDao {


    @Override
    public PersonAccountStatesBean getPersonAccountStates(final String partnerNo, final String certificateNo
            , final CertificateType certificateType) {
        final Select stmt;
        stmt = SQLs.query()
                .select(list -> {
                    //due to selectionList depend on criteria context,so you couldn't create selectionList before SQLs.query().
                    //because SQLs.field("pu", BankUser_.id) depend criteria context
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
                .where(SQLs.field("p", BankUser_.userNo).equal(partnerNo))
                .and(Certificate_.certificateNo.equal(certificateNo))
                .and(Certificate_.certificateType.equalLiteral(CertificateType.PERSON))
                .limit(1)
                .asQuery();

        return this.sessionContext.currentSession().selectOne(stmt, PersonAccountStatesBean.class);
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
                .join(BankAccount_.T, "a").on(BankAccount_.userId.equal(BankUser_.id))
                .join(Certificate_.T, "c").on(Certificate_.id.equal(BankUser_.certificateId))
                .where(RegisterRecord_.requestNo.equalLiteral(requestNo))
                .and(Certificate_.certificateNo.equal(certificateNo))
                .and(Certificate_.certificateType.equalLiteral(certificateType))
                .and(BankUser_.userType.equalLiteral(BankUserType.INVEST_PARTNER))
                .and(BankAccount_.accountType.equalLiteral(BankAccountType.PARTNER))
                .asQuery();
        return this.sessionContext.currentSession().selectOneAsMap(stmt);
    }


}
