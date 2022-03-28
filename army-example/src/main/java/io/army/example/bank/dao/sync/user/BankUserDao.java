package io.army.example.bank.dao.sync.user;

import io.army.example.bank.domain.user.BankUserType;
import io.army.example.bank.domain.user.Certificate;
import io.army.example.bank.domain.user.CertificateType;
import io.army.example.common.Pair;
import io.army.example.common.SyncBaseDao;
import org.springframework.lang.Nullable;

public interface BankUserDao extends SyncBaseDao {

    boolean isExists(String certificateNo, CertificateType certificateType, BankUserType userType);

    @Nullable
    <T extends Certificate<T>> T getCertificate(String certificateNo, CertificateType certificateType
            , Class<T> domainType);

    @Nullable
    Pair<Long, BankUserType> getUserPair(String userNo);

}
