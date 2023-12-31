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

import io.army.example.bank.service.reactive.region.BankRegionService;
import io.army.example.common.BaseService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

@RequestMapping("bank/region")
@RestController("regionController")
public class RegionController implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private BankRegionService regionService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        final ApplicationContext context = this.applicationContext;
        final Environment env = context.getEnvironment();

        if (env.acceptsProfiles(Profiles.of(BaseService.SYNC))) {
            this.regionService = context.getBean("bankRegionServiceAdapter", BankRegionService.class);
        } else {
            this.regionService = context.getBean("bankRegionService", BankRegionService.class);
        }
    }

    @RequestMapping(value = "createRegionIfNotExists", method = RequestMethod.POST)
    public Flux<Map<String, Object>> createRegionIfNotExists() {
        return this.regionService.createRegionIfNotExists();
    }


}
