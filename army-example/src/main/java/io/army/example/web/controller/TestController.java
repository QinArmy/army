package io.army.example.web.controller;

import io.army.dialect.mysql.MySQLTypes;
import io.army.example.service.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController("testController")
@RequestMapping("/test")
public class TestController implements InitializingBean, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    private ApplicationContext applicationContext;

    private BaseService baseService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final ApplicationContext context = this.applicationContext;
        final Environment env = context.getEnvironment();

        if (env.acceptsProfiles(Profiles.of(BaseService.SYNC))) {
            this.baseService = context.getBean(BaseService.class, "baseServiceAdapter");
        } else {
            this.baseService = context.getBean(BaseService.class, "baseService");
        }
    }


    @RequestMapping(value = "save/mysql/types", method = RequestMethod.POST)
    public Mono<MySQLTypes> saveMySQLTypes() {
        final MySQLTypes mySQLTypes = new MySQLTypes();
        mySQLTypes.myBigint = 8870L;
        mySQLTypes.myBit64 = -1L;
        return this.baseService.save(mySQLTypes)
                .thenReturn(mySQLTypes);
    }


}
