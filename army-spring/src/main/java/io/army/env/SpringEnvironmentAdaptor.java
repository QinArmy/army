package io.army.env;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public class SpringEnvironmentAdaptor extends StandardEnvironment {

    public SpringEnvironmentAdaptor(Environment environment) {
        super(environment);
    }


    @Override
    protected ConfigurableEnvironment obtainConfigurableEnvironment() {
        if (this.env instanceof ConfigurableEnvironment) {
            return (ConfigurableEnvironment) this.env;
        }
        throw new IllegalStateException(String.format("environment isn't %s type."
                , ConfigurableEnvironment.class.getName()));
    }
}
