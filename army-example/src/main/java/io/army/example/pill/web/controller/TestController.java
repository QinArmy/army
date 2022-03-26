package io.army.example.pill.web.controller;

import io.army.example.common.BaseService;
import io.army.example.dialect.mysql.MySQLTypes;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

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
    public void afterPropertiesSet() {
        final ApplicationContext context = this.applicationContext;
        final Environment env = context.getEnvironment();

        if (env.acceptsProfiles(Profiles.of(BaseService.SYNC))) {
            this.baseService = context.getBean("pillBaseServiceAdapter", BaseService.class);
        } else {
            this.baseService = context.getBean("pillBaseService", BaseService.class);
        }
    }


    @RequestMapping(value = "save/mysql/types/save", method = RequestMethod.POST)
    public Mono<MySQLTypes> saveMySQLTypes() {
        final MySQLTypes mySQLTypes = new MySQLTypes();
        mySQLTypes.myBigint = 8870L;
        mySQLTypes.myBit64 = -1L;
        return this.baseService.save(mySQLTypes)
                .thenReturn(mySQLTypes);
    }

    @RequestMapping(value = "save/mysql/types/findById", method = RequestMethod.GET)
    public Mono<MySQLTypes> findMySQLTypesById(@RequestParam(value = "id") Long id) {
        return this.baseService.findById(MySQLTypes.class, id);
    }

    @RequestMapping(value = "save/mysql/types/findByIdAsMap", method = RequestMethod.GET)
    public Mono<Map<String, Object>> findMySQLTypesByIdAsMap(@RequestParam(value = "id") Long id) {
        return this.baseService.findByIdAsMap(MySQLTypes.class, id);
    }


}
