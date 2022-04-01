package io.army.example.common;

import io.army.dialect.Dialect;
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
            for (Dialect dialect : Dialect.values()) {
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
