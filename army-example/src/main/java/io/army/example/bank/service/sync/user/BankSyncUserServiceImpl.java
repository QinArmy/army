package io.army.example.bank.service.sync.user;

import io.army.example.bank.bean.PersonAccountStatesBean;
import io.army.example.bank.dao.sync.user.BankAccountDao;
import io.army.example.bank.dao.sync.user.BankUserDao;
import io.army.example.bank.domain.account.BankAccount;
import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.*;
import io.army.example.bank.service.BankExceptions;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.bank.service.sync.region.BankSyncRegionService;
import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.BaseService;
import io.army.example.common.CommonUtils;
import io.army.example.common.Gender;
import io.army.example.common.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service("bankSyncUserService")
@Profile(BaseService.SYNC)
public class BankSyncUserServiceImpl extends BankSyncBaseService implements BankSyncUserService {

    private BankUserDao userDao;

    private BankAccountDao accountDao;

    private BankSyncRegionService regionService;

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public Map<String, Object> personRegister(final PersonRegisterForm form) {
        final String partnerNo, certificateNo;
        partnerNo = form.getPartnerUserNo();
        certificateNo = form.getCertificateNo();

        final PersonAccountStatesBean statesBean;
        statesBean = this.accountDao.getPersonAccountStates(partnerNo, certificateNo, form.getCertificateType());
        if (statesBean == null) {
            throw BankExceptions.partnerNotExists(partnerNo);
        }
        if (statesBean.userId != null) {
            throw BankExceptions.duplicationAccount(partnerNo, statesBean.userNo);
        }
        final PersonCertificate certificate;
        certificate = createPersonCertificate(form);
        this.baseDao.save(certificate);

        final Person user;
        user = createPersonUser(statesBean, form, certificate);
        this.baseDao.save(certificate);
        final BankAccount account;
        account = createPersonAccount(user, form.getAccountType());
        this.baseDao.save(account);

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

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public Map<String, Object> registerRequest(final @Nullable String partnerNo) {
        final long partnerId;
        if (partnerNo == null) {
            partnerId = 0L;
        } else {
            final Pair<Long, BankUserType> pair;
            pair = this.userDao.getUserPair(partnerNo);
            if (pair == null || !pair.getSecond().isPartner()) {
                throw BankExceptions.partnerNotExists(partnerNo);
            }
            partnerId = pair.getFirst();
        }
        final LocalDateTime now = LocalDateTime.now();
        final RegisterRecord r;
        r = new RegisterRecord()
                .setPartnerId(partnerId)
                .setStatus(RecordStatus.CREATED)
                .setDeadline(now.plusMinutes(30));

        this.baseDao.save(r);
        final Captcha captcha;
        captcha = new Captcha()
                .setCaptcha(CommonUtils.randomCaptcha())
                .setPartnerId(r.getPartnerId())
                .setRequestNo(r.getRequestNo())
                .setDeadline(now.plusMinutes(10));

        this.baseDao.save(captcha);

        return captchaResult(captcha);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public Map<String, Object> partnerRegister(final EnterpriseRegisterForm form) {
        final String requestNo = form.getRequestNo();
        final RegisterRecord r;
        r = this.baseDao.getByUnique(RegisterRecord.class, "requestNo", requestNo);
        if (r == null || r.getPartnerId() != 0L) {
            throw BankExceptions.invalidRequestNo(requestNo);
        }
        final Map<String, Object> resultMap;
        switch (r.getStatus()) {
            case CREATED: {
                final LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(r.getDeadline())) {
                    throw BankExceptions.registerRecordDeadline(requestNo, r.getDeadline());
                }
                final Captcha captcha;
                captcha = this.baseDao.getByUnique(Captcha.class, "requestNo", requestNo);
                if (captcha == null) {
                    throw BankExceptions.invalidRequestNo(requestNo);
                }
                if (!captcha.getCaptcha().equals(form.getCaptcha()) || now.isAfter(captcha.getDeadline())) {
                    throw BankExceptions.errorCaptcha(requestNo, form.getCaptcha());
                }
                if (this.userDao.isExists(form.getCertificateNo(), CertificateType.ENTERPRISE, form.getUserType())) {
                    throw BankExceptions.duplicationPartner(form.getUserType());
                }
                resultMap = handlePartnerRegister(r, form);
            }
            break;
            case SUCCESS: {
                final Map<String, Object> temp;
                temp = this.accountDao.getPartnerAccountStatus(requestNo, form.getCertificateNo()
                        , CertificateType.ENTERPRISE);
                if (temp == null) {
                    throw BankExceptions.invalidRequestNo(requestNo);
                }
                resultMap = Collections.unmodifiableMap(temp);
            }
            break;
            case FAILURE:
                throw BankExceptions.registerRecordFailed(requestNo);
            default:
                throw BankExceptions.unexpectedEnum(r.getStatus());
        }
        return resultMap;
    }


    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public Map<String, Object> nextCaptcha(final String requestNo) {
        final Captcha captcha;
        captcha = this.baseDao.getByUnique(Captcha.class, "requestNo", requestNo);
        if (captcha == null) {
            throw BankExceptions.invalidRequestNo(requestNo);
        }
        captcha.setCaptcha(CommonUtils.randomCaptcha())
                .setDeadline(LocalDateTime.now().plusMinutes(10));
        return captchaResult(captcha);
    }

    /*################################## blow setter method ##################################*/

    @Autowired
    public void setUserDao(@Qualifier("bankSyncUserDao") BankUserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAccountDao(@Qualifier("bankSyncAccountDao") BankAccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Autowired
    public void setRegionService(@Qualifier("bankSyncRegionService") BankSyncRegionService regionService) {
        this.regionService = regionService;
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

    private static BankAccount createPersonAccount(final Person user, final BankAccountType accountType) {
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

    private static Map<String, Object> captchaResult(Captcha captcha) {
        final Map<String, Object> result = new HashMap<>(6);
        result.put("requestNo", captcha.getRequestNo());
        result.put("captcha", captcha.getCaptcha());
        result.put("deadline", captcha.getDeadline());
        return Collections.unmodifiableMap(result);
    }

    private Map<String, Object> handlePartnerRegister(RegisterRecord r, EnterpriseRegisterForm form) {
        final Long cityId;
        cityId = this.regionService.getRegionId(form.getCity(), RegionType.CITY);
        if (cityId == null) {
            throw BankExceptions.regionNotExists(r.getRequestNo(), form.getCity());
        }
        EnterpriseCertificate certificate;
        certificate = this.userDao.getCertificate(form.getCertificateNo(), CertificateType.ENTERPRISE
                , EnterpriseCertificate.class);
        if (certificate == null) {
            certificate = createPartnerCertificate(form);
            this.userDao.save(certificate);
        }
        final InvestPartnerUser u;
        u = new InvestPartnerUser()

                .setCertificateId(certificate.getId())
                .setNickName(form.getName())
                .setCityId(cityId)
                .setPartnerUserId(0L);

        this.userDao.save(u);

        final BankAccount a;
        a = new BankAccount()

                .setUserId(u.getId())
                .setUserType(u.getUserType())
                .setAccountType(BankAccountType.PARTNER);
        this.accountDao.save(a);

        final LocalDateTime now = LocalDateTime.now();
        r.setStatus(RecordStatus.SUCCESS)
                .setHandleTime(now)
                .setCompletionTime(now)
                .setUserId(u.getId());

        final Map<String, Object> map = new HashMap<>();

        map.put("requestNo", r.getRequestNo());
        map.put("userNo", u.getId());
        map.put("userType", u.getUserType());
        map.put("accountNo", a.getAccountNo());

        map.put("accountType", a.getAccountType());
        return Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("unchecked")
    private EnterpriseCertificate createPartnerCertificate(EnterpriseRegisterForm form) {

        Certificate<?> legalPersonCertificate;
        legalPersonCertificate = this.userDao.getCertificate(form.getLegalPersonCertificateNo()
                , CertificateType.PERSON, Certificate.class);
        if (legalPersonCertificate == null) {
            final String legalPersonCertificateNo = form.getLegalPersonCertificateNo();
            legalPersonCertificate = new PersonCertificate()

                    .setCertificateNo(legalPersonCertificateNo)
                    .setBirthday(CommonUtils.birthdayFrom(legalPersonCertificateNo))
                    .setGender(Gender.fromCertificateNo(legalPersonCertificateNo))
                    .setNation("")

                    .setSubjectName(form.getLegalPerson());
            this.userDao.save(legalPersonCertificate);
        }
        return new EnterpriseCertificate()

                .setCreditCode(form.getCreditCode())
                .setCertificateNo(form.getCertificateNo())
                .setRegisterDay(form.getRegisterDay())
                .setSubjectName(form.getName())

                .setLegalPersonCertificateId(legalPersonCertificate.getId());
    }


}
