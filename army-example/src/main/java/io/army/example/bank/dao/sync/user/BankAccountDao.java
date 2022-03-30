package io.army.example.bank.dao.sync.user;

import io.army.example.bank.domain.user.CertificateType;
import io.army.example.common.SyncBaseDao;
import org.springframework.lang.Nullable;

import java.util.Map;

public interface BankAccountDao extends SyncBaseDao {

    @Nullable
    Map<String, Object> getRegisterUserInfo(String requestNo);


    @Nullable
    Map<String, Object> getPartnerAccountStatus(String requestNo, String certificateNo, CertificateType certificateType);


}
