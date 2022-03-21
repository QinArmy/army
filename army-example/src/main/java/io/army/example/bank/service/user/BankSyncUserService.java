package io.army.example.bank.service.user;

import io.army.example.bank.service.result.RegisterResult;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.SyncBaseService;

public interface BankSyncUserService extends SyncBaseService {

    RegisterResult personRegister(PersonRegisterForm form);


}
