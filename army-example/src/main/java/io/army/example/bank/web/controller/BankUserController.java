package io.army.example.bank.web.controller;

import io.army.example.bank.service.reactive.user.BankUserService;
import io.army.example.common.BaseService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
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
        final ApplicationContext context = this.applicationContext;
        final Environment env = context.getEnvironment();

        if (env.acceptsProfiles(Profiles.of(BaseService.SYNC))) {
            this.userService = context.getBean("bankUserServiceAdapter", BankUserService.class);
        } else {
            this.userService = context.getBean("bankUserService", BankUserService.class);
        }
    }

    @RequestMapping(value = "partnerRegisterRequest", method = RequestMethod.GET)
    public Mono<Map<String, Object>> partnerRegisterRequest() {
        return this.userService.partnerRegisterRequest();
    }

    @RequestMapping(value = "nextCaptcha", method = RequestMethod.GET)
    public Mono<Map<String, Object>> nextCaptcha(@RequestParam("requestNo") String requestNo) {
        return this.userService.nextCaptcha(requestNo);
    }


}
