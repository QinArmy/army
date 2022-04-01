package io.army.example.bank.web.controller;

import io.army.example.bank.service.reactive.user.BankUserService;
import io.army.example.bank.web.form.EnterpriseRegisterForm;
import io.army.example.bank.web.form.PersonRegisterForm;
import io.army.example.common.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequestMapping("bank/user")
@RestController
public class BankUserController implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BankUserService userService;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        this.userService = BeanUtils.getService("bankUserService", BankUserService.class, this.applicationContext);
    }

    @RequestMapping(value = "registerRequest", method = RequestMethod.GET)
    public Mono<Map<String, Object>> registerRequest(@RequestParam(value = "partnerNo", required = false) String partnerNo) {
        return this.userService.registerRequest(partnerNo);
    }

    @RequestMapping(value = "nextCaptcha", method = RequestMethod.GET)
    public Mono<Map<String, Object>> nextCaptcha(@RequestParam("requestNo") String requestNo) {
        return this.userService.nextCaptcha(requestNo);
    }

    @RequestMapping(value = "partnerRegister", method = RequestMethod.POST)
    public Mono<Map<String, Object>> partnerRegister(@Validated EnterpriseRegisterForm form) {
        return this.userService.partnerRegister(form);
    }

    @RequestMapping(value = "personRegister", method = RequestMethod.POST)
    public Mono<Map<String, Object>> personRegister(@Validated PersonRegisterForm form) {
        return this.userService.personRegister(form);
    }


}
