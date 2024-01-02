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

package io.army.driver;

import io.jdbd.Driver;
import io.jdbd.session.DatabaseSessionFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

public class JdbdTests {

    private static DatabaseSessionFactory sessionFactory;

    @BeforeClass
    public void beforeClassCreateFactory() {
        final String url;
        final Map<String, Object> map = new HashMap<>();

        url = "jdbd:mysql://localhost:3306/army_bank?factoryWorkerCount=30";

        map.put(Driver.USER, "army_w");
        map.put(Driver.PASSWORD, "army123");


        sessionFactory = Driver.findDriver(url).forDeveloper(url, map);
    }


    @Test
    public void session() {

        Mono.from(sessionFactory.localSession())
                .flatMap(session -> Mono.from(session.close()))
                .block();
    }

}
