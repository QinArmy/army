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

package io.army.example.common;

import io.army.dialect.MySQLDialect;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public abstract class BeanUtils {

    public static final String STANDARD = "Standard";

    public static final String MY_SQL57 = "MySQL57";

    protected BeanUtils() {
        throw new UnsupportedOperationException();
    }

    public static <T> T getDao(final String nameFormat, final Class<T> beanType, final ApplicationContext cxt) {
        final Environment env = cxt.getEnvironment();
        String beanName = null;
        if (!env.acceptsProfiles(Profiles.of(STANDARD))) {
            for (MySQLDialect dialect : MySQLDialect.values()) {
                if (env.acceptsProfiles(Profiles.of(dialect.name()))) {
                    beanName = String.format(nameFormat, dialect.name());
                    break;
                }
            }
        }
        if (beanName == null) {
            beanName = String.format(nameFormat, STANDARD);
        }
        return cxt.getBean(beanName, beanType);
    }

    public static <T> T getService(final String name, final Class<T> beanType, final ApplicationContext cxt) {
        final Environment env = cxt.getEnvironment();
        final String beanName;
        if (env.acceptsProfiles(Profiles.of(BaseService.SYNC))) {
            beanName = name + "Adapter";
        } else {
            beanName = name;
        }
        return cxt.getBean(beanName, beanType);
    }


}
