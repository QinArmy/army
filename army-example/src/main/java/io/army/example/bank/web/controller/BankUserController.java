/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
