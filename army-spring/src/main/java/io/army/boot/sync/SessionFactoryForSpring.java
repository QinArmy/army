package io.army.boot.sync;

import io.army.SessionFactoryException;
import io.army.sync.SessionFactoryAdvice;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

class SessionFactoryForSpring extends SessionFactoryImpl implements InitializingBean, DisposableBean {

    private SessionFactoryAdvice compositeFactoryAdvice;

    SessionFactoryForSpring(SessionFactoryBuilderImpl factoryBuilder) throws SessionFactoryException {
        super(factoryBuilder);
        this.compositeFactoryAdvice = factoryBuilder.getCompositeSessionFactoryAdvice();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (super.initializeSessionFactory() && this.compositeFactoryAdvice != null) {
            this.compositeFactoryAdvice.afterInitialize(this);
            this.compositeFactoryAdvice = null;
        }
    }

    @Override
    public void destroy() throws Exception {
        super.close();
    }
}
