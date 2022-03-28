package io.army.example.bank.service.reactive.user;

import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.BaseService;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface BankUserService extends BaseService {

    Mono<Map<String, Object>> personRegister(PersonRegisterForm form);

    Mono<Map<String, Object>> registerRequest(String partnerNo);

    Mono<Map<String, Object>> nextCaptcha(String requestNo);

    Mono<Map<String, Object>> partnerRegister(EnterpriseRegisterForm form);

}
