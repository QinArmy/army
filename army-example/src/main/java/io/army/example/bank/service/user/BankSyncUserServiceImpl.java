package io.army.example.bank.service.user;

import io.army.example.bank.ban.PersonAccountStatesBean;
import io.army.example.bank.dao.sync.user.BankUserDao;
import io.army.example.bank.domain.account.AccountType;
import io.army.example.bank.domain.account.BankAccount;
import io.army.example.bank.domain.user.Person;
import io.army.example.bank.domain.user.PersonCertificate;
import io.army.example.bank.service.BankExceptions;
import io.army.example.bank.service.BankSyncBaseService;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.CommonUtils;
import io.army.example.common.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("bankSyncUserService")
public class BankSyncUserServiceImpl extends BankSyncBaseService implements BankSyncUserService {

    private BankUserDao userDao;

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public Map<String, Object> personRegister(final PersonRegisterForm form) {
        final String partnerNo, certificateNo;
        partnerNo = form.getPartnerUserNo();
        certificateNo = form.getCertificateNo();

        final PersonAccountStatesBean statesBean;
        statesBean = userDao.getPersonAccountStates(partnerNo, certificateNo, form.getCertificateType());
        if (statesBean == null) {
            throw BankExceptions.partnerNotExists(partnerNo);
        }
        if (statesBean.userId != null) {
            throw BankExceptions.duplicationUser(partnerNo, statesBean.userNo);
        }
        final PersonCertificate certificate;
        certificate = createPersonCertificate(form);
        this.userDao.save(certificate);

        final Person user;
        user = createPersonUser(statesBean, form, certificate);
        this.userDao.save(certificate);
        final BankAccount account;
        account = createPersonAccount(user, form.getAccountType());
        this.userDao.save(account);

        final Map<String, Object> result = new HashMap<>();

        result.put("userNo", user.getUserNo());
        result.put("userType", user.getUserType());
        result.put("accountNo", account.getAccountNo());
        result.put("accountType", account.getAccountType());

        result.put("partnerNo", statesBean.partnerNo);
        result.put("requestTime", user.getCreateTime());
        result.put("completionTime", user.getCreateTime());

        return Collections.unmodifiableMap(result);
    }


    @Autowired
    public void setUserDao(@Qualifier("bankSyncUserDao") BankUserDao userDao) {
        this.userDao = userDao;
    }


    private static PersonCertificate createPersonCertificate(PersonRegisterForm form) {
        final String certificateNo;
        certificateNo = form.getCertificateNo();
        return new PersonCertificate()
                .setCertificateNo(form.getCertificateNo())
                .setGender(Gender.fromCertificateNo(certificateNo))
                .setNation("")
                .setSubjectName(form.getName())
                .setBirthday(CommonUtils.birthdayFrom(certificateNo))
                ;
    }

    private static Person createPersonUser(PersonAccountStatesBean statesBean, PersonRegisterForm form
            , PersonCertificate certificate) {
        return new Person()
                .setPartnerUserId(statesBean.partnerUserId)
                .setPhone(form.getPhone())
                .setNickName(form.getPhone())
                .setFromPartnerType(statesBean.partnerUserType)

                .setCertificateId(certificate.getId())
                //.setBirthday(certificate.getBirthday())
                ;
    }

    private static BankAccount createPersonAccount(final Person user, final AccountType accountType) {
        switch (accountType) {
            case LENDER:
            case BORROWER:
                //no-op
                break;
            default: {
                String m = String.format("%s don't support person user", accountType);
                throw new IllegalArgumentException(m);
            }
        }
        return new BankAccount()
                .setUserType(user.getUserType())
                .setUserId(user.getId())
                .setAccountType(accountType);

    }


}
