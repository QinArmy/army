package io.army.env;

import org.springframework.core.env.ConfigurableEnvironment;

public class SpringEnvironmentAdaptor extends StandardEnvironment {

    public SpringEnvironmentAdaptor(ConfigurableEnvironment environment) {
        super(environment);
    }
}
