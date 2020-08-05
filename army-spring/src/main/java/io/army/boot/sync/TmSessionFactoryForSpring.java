package io.army.boot.sync;

import io.army.sync.SessionFactoryAdvice;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

class TmSessionFactoryForSpring extends TmSessionFactoryImpl implements InitializingBean, DisposableBean {

    private SessionFactoryAdvice compositeFactoryAdvice;

    TmSessionFactoryForSpring(TmSessionFactionBuilderImpl builder) {
        super(builder);
        this.compositeFactoryAdvice = builder.getCompositeSessionFactoryAdvice();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (super.initializeTmSessionFactory() && this.compositeFactoryAdvice != null) {
            this.compositeFactoryAdvice.afterInitialize(this);
            this.compositeFactoryAdvice = null;
        }
    }

    @Override
    public void destroy() throws Exception {
        super.close();
    }


}
