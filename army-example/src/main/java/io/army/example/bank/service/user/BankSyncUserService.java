package io.army.example.bank.service.user;

import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.SyncBaseService;

import java.util.Map;

public interface BankSyncUserService extends SyncBaseService {

    Map<String, Object> personRegister(PersonRegisterForm form);

    //Map<String,Object>  personUnregister();


}
