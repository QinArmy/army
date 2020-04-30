package io.army.generator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

public class GeneratorTests {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorTests.class);

    @Test
    public void generatorMapping() {
/*        final long startTime = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();

        map.put(GenericSessionFactory.PACKAGE_TO_SCAN, "com.example.generator");

        GenericSessionFactory sessionFactory = BootstrapTests.builder(map)
                .build();
        LOG.info("tableMeta generator chain:");

        for (Map.Entry<TableMeta<?>, List<FieldMeta<?, ?>>> e : sessionFactory.tableGeneratorChain().entrySet()) {
            StringBuilder builder = new StringBuilder("tableMeta:");
            builder.append(e.getKey().tableName())
                    .append("\n")
            ;
            Iterator<FieldMeta<?, ?>> iterator = e.getValue().iterator();

            for (FieldMeta<?, ?> fieldMeta; iterator.hasNext(); ) {
                fieldMeta = iterator.next();
                builder.append(fieldMeta.propertyName())
                ;
                if (iterator.hasNext()) {
                    builder.append("\n");
                }
            }
            builder.append("\n\n");
            LOG.info("{}", builder.toString());
        }
        LOG.info("cost {} ms", System.currentTimeMillis() - startTime);*/
    }

    @Test
    public void flux() {
        List<String> list = Collections.emptyList();
        Object result = Flux.fromIterable(list)
                .map(this::numMap)
                .concatMap(this::mapObj)
                .next()
                .switchIfEmpty(Mono.defer(() -> Mono.just("simonyi")))
                .block();
        LOG.info("result:{}", result);
    }

    private String numMap(String s) {
        LOG.info("first " + s);
        return s;
    }

    private Mono<Object> mapObj(String num) {
        LOG.info("mapObj" + num);
        if (num.equals("3")) {
            return Mono.just("army" + num);
        } else {
            return Mono.empty();
        }

    }
}
