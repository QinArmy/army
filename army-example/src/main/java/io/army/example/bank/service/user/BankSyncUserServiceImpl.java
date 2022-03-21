package io.army.example.bank.service.user;

import io.army.example.bank.dao.sync.user.BankUserDao;
import io.army.example.bank.service.BankSyncBaseService;
import io.army.example.bank.service.result.RegisterResult;
import io.army.example.bank.web.form.PersonRegisterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service("bankSyncUserService")
public class BankSyncUserServiceImpl extends BankSyncBaseService implements BankSyncUserService {

    private BankUserDao userDao;

    @Transactional(value = TX_MANAGER, isolation = Isolation.READ_UNCOMMITTED)
    @Override
    public RegisterResult personRegister(final PersonRegisterForm form) {

        return null;
    }


    @Autowired
    public void setUserDao(@Qualifier("bankSyncUserDao") BankUserDao userDao) {
        this.userDao = userDao;
    }


}
