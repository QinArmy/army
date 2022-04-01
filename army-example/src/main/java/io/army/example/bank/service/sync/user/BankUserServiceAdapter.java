package io.army.example.bank.service.sync.user;

import io.army.example.bank.service.reactive.user.BankUserService;
import io.army.example.bank.service.sync.BankBaseServiceAdapter;
import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component("bankUserServiceAdapter")
@Profile(BaseService.SYNC)
public class BankUserServiceAdapter extends BankBaseServiceAdapter implements BankUserService {

    private BankSyncUserService userService;


    @Override
    public Mono<Map<String, Object>> personRegister(PersonRegisterForm form) {
        return Mono.defer(() -> Mono.just(this.userService.personRegister(form)));
    }

    @Override
    public Mono<Map<String, Object>> registerRequest(String partnerNo) {
        return Mono.defer(() -> Mono.just(this.userService.registerRequest(partnerNo)));
    }

    @Override
    public Mono<Map<String, Object>> nextCaptcha(String requestNo) {
        return Mono.defer(() -> Mono.just(this.userService.nextCaptcha(requestNo)));
    }

    @Override
    public Mono<Map<String, Object>> partnerRegister(EnterpriseRegisterForm form) {
        return Mono.defer(() -> Mono.just(this.userService.partnerRegister(form)));
    }

    @Autowired
    public void setUserService(@Qualifier("bankSyncUserService") BankSyncUserService userService) {
        this.userService = userService;
    }


}
