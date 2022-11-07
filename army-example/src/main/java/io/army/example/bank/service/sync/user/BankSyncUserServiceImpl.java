package io.army.example.bank.service.sync.user;

import io.army.example.bank.BankException;
import io.army.example.bank.bean.BankCode;
import io.army.example.bank.dao.sync.user.BankAccountDao;
import io.army.example.bank.dao.sync.user.BankUserDao;
import io.army.example.bank.domain.account.BankAccount;
import io.army.example.bank.domain.account.BankAccountType;
import io.army.example.bank.domain.user.*;
import io.army.example.bank.service.BankExceptions;
import io.army.example.bank.service.InvalidRequestNoException;
import io.army.example.bank.service.sync.BankSyncBaseService;
import io.army.example.bank.service.sync.region.BankSyncRegionService;
import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOG = LoggerFactory.getLogger(BankSyncUserServiceImpl.class);

    private BankUserDao userDao;

    private BankAccountDao accountDao;

    private BankSyncRegionService regionService;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.userDao = BeanUtils.getDao("bankSync%sUserDao", BankUserDao.class, this.applicationContext);
        this.accountDao = BeanUtils.getDao("bankSync%sAccountDao", BankAccountDao.class, this.applicationContext);
    }

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_COMMITTED)
    @Override
    public Map<String, Object> personRegister(final PersonRegisterForm form) {
        final String requestNo, certificateNo;
        requestNo = form.getRequestNo();
        certificateNo = form.getCertificateNo();

        final RegisterRecord record;
        record = this.baseDao.getByUnique(RegisterRecord.class, REQUEST_NO, requestNo);
        if (record == null) {
            throw BankExceptions.invalidRequestNo(requestNo);
        }
        final Captcha captcha;
        captcha = this.baseDao.getByUnique(Captcha.class, REQUEST_NO, requestNo);
        if (captcha == null) {
            throw BankExceptions.invalidRequestNo(requestNo);
        }
        Map<String, Object> result;
        // below don't invoke other Transactional method. so try cache.
        try {
            switch (record.getStatus()) {
                case CREATED: {
                    final LocalDateTime now = LocalDateTime.now();
                    if (now.isAfter(record.getDeadline())) {
                        throw BankExceptions.registerRecordDeadline(requestNo, record.getDeadline());
                    }
                    if (now.isAfter(captcha.getDeadline()) || !captcha.getCaptcha().equals(form.getCaptcha())) {
                        throw BankExceptions.errorCaptcha(requestNo, form.getCaptcha());
                    }
                    if (this.userDao.isExists(certificateNo, form.getCertificateType(), BankUserType.PERSON)) {
                        throw BankExceptions.duplicationUser(BankUserType.PERSON);
                    }
                    result = handlePersonRegister(record, form);
                }
                break;
                case SUCCESS: {
                    result = this.accountDao.getRegisterUserInfo(requestNo);
                    if (result == null) {
                        // deleted
                        throw BankExceptions.invalidRequestNo(requestNo);
                    }
                }
                break;
                case FAILURE:
                    result = createRecordFailureMap(record);
                    break;
                default:
                    throw BankExceptions.unexpectedEnum(record.getStatus());
            }
            return Collections.unmodifiableMap(result);
        } catch (InvalidRequestNoException e) {
            throw e;
        } catch (BankException e) {
            record.setStatus(RecordStatus.FAILURE)
                    .setBankCode(e.bankCode)
                    .setFailureMessage(e.getMessage());
            result = createRecordFailureMap(record);
        } catch (RuntimeException e) {
            LOG.error("person register unknown error.", e);
            record.setStatus(RecordStatus.FAILURE)
                    .setBankCode(BankCode.UNKNOWN_ERROR)
                    .setFailureMessage(e.getMessage());
            result = createRecordFailureMap(record);
        }
        return result;
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
        r = this.baseDao.getByUnique(RegisterRecord.class, REQUEST_NO, requestNo);
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
                captcha = this.baseDao.getByUnique(Captcha.class, REQUEST_NO, requestNo);
                if (captcha == null) {
                    throw BankExceptions.invalidRequestNo(requestNo);
                }
                if (!captcha.getCaptcha().equals(form.getCaptcha()) || now.isAfter(captcha.getDeadline())) {
                    throw BankExceptions.errorCaptcha(requestNo, form.getCaptcha());
                }
                if (this.userDao.isExists(form.getCertificateNo(), CertificateType.ENTERPRISE, BankUserType.PARTNER)) {
                    throw BankExceptions.duplicationUser(BankUserType.PARTNER);
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
    public void setRegionService(@Qualifier("bankSyncRegionService") BankSyncRegionService regionService) {
        this.regionService = regionService;
    }


    private Map<String, Object> handlePersonRegister(RegisterRecord record, PersonRegisterForm form) {
        final String partnerNo = form.getPartnerUserNo();
        final Pair<Long, BankUserType> partnerPair;
        partnerPair = this.userDao.getUserPair(partnerNo);
        if (partnerPair == null) {
            throw BankExceptions.partnerNotExists(partnerNo);
        }
        if (!record.getPartnerId().equals(partnerPair.first)) {
            throw BankExceptions.partnerNotExists(partnerNo);
        }

        PersonCertificate certificate;
        certificate = this.userDao.getCertificate(form.getCertificateNo()
                , form.getCertificateType(), PersonCertificate.class);
        if (certificate == null) {
            certificate = createPersonCertificate(form);
            this.baseDao.save(certificate);
        }
        final BankPerson user;
        user = new BankPerson()
                .setPartnerUserId(partnerPair.first)
                .setPhone(form.getPhone())
                .setNickName(form.getPhone())
                .setFromPartnerType(partnerPair.second)
                .setCertificateId(certificate.getId())
                .setBirthday(certificate.getBirthday())
                .setRegisterRecordId(record.getId());
        this.baseDao.save(user);

        final BankAccount account;
        account = createPersonAccount(user, form.getAccountType())
                .setRegisterRecordId(record.getId());
        this.baseDao.save(account);

        final LocalDateTime now = LocalDateTime.now();
        record.setStatus(RecordStatus.SUCCESS)
                .setUserId(user.getId())
                .setHandleTime(now)
                .setCompletionTime(now);

        final Map<String, Object> result = new HashMap<>();

        result.put("userNo", user.getUserNo());
        result.put("userType", user.getUserType());
        result.put("accountNo", account.getAccountNo());
        result.put("accountType", account.getAccountType());

        result.put("partnerNo", partnerNo);
        result.put("requestTime", user.getCreateTime());
        result.put("completionTime", user.getCreateTime());
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
        final PartnerUser u;
        u = new PartnerUser()

                .setCertificateId(certificate.getId())
                .setNickName(form.getName())
                .setCityId(cityId)
                .setPartnerUserId(0L)
                .setRegisterRecordId(r.getId());

        this.userDao.save(u);

        final Long userId;
        userId = u.getId();
        final BankAccount account;
        account = new BankAccount()

                .setUserId(userId)
                .setUserType(u.getUserType())
                .setAccountType(BankAccountType.PARTNER)
                .setRegisterRecordId(r.getId());

        this.accountDao.save(account);

        final LocalDateTime now = LocalDateTime.now();
        r.setStatus(RecordStatus.SUCCESS)
                .setHandleTime(now)
                .setCompletionTime(now)
                .setUserId(userId);

        final Map<String, Object> map = new HashMap<>();

        map.put("requestNo", r.getRequestNo());
        map.put("userNo", u.getId());
        map.put("userType", u.getUserType());
        map.put("accountNo", account.getAccountNo());

        map.put("accountType", account.getAccountType());
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


    private static PersonCertificate createPersonCertificate(PersonRegisterForm form) {
        final String certificateNo;
        certificateNo = form.getCertificateNo();
        return new PersonCertificate()
                .setCertificateNo(certificateNo)
                .setGender(Gender.fromCertificateNo(certificateNo))
                .setNation("")
                .setSubjectName(form.getName())
                .setBirthday(CommonUtils.birthdayFrom(certificateNo))
                ;
    }


    private static BankAccount createPersonAccount(final BankPerson user, final BankAccountType accountType) {
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

    private static Map<String, Object> createRecordFailureMap(RegisterRecord record) {
        assert record.getStatus() == RecordStatus.FAILURE;
        final Map<String, Object> result = new HashMap<>(6);
        result.put("code", record.getBankCode());
        result.put("message", record.getFailureMessage());
        result.put("requestNo", record.getRequestNo());
        return Collections.unmodifiableMap(result);
    }


}
