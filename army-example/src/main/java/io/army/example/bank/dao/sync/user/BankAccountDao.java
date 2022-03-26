package io.army.example.bank.dao.sync.user;

import io.army.example.bank.ban.PersonAccountStatesBean;
import io.army.example.bank.domain.user.CertificateType;
import io.army.example.common.BaseDao;
import org.springframework.lang.Nullable;

public interface BankAccountDao extends BaseDao {

    @Nullable
    PersonAccountStatesBean getPersonAccountStates(String partnerNo, String certificateNo, CertificateType certificateType);


}
