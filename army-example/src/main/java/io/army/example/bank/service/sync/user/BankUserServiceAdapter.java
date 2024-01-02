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
