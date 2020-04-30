package io.army.env;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public class SpringEnvironmentAdaptor extends StandardEnvironment {

    public SpringEnvironmentAdaptor(Environment environment) {
        super((ConfigurableEnvironment) environment);
    }
}
