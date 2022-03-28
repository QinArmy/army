package io.army.example.bank.service.sync.user;

import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.SyncBaseService;
import org.springframework.lang.Nullable;

import java.util.Map;

public interface BankSyncUserService extends SyncBaseService {

    Map<String, Object> personRegister(PersonRegisterForm form);

    //Map<String,Object>  personUnregister();

    Map<String, Object> registerRequest(@Nullable String partnerNo);

    Map<String, Object> partnerRegister(EnterpriseRegisterForm form);

    Map<String, Object> nextCaptcha(String requestNo);


}
