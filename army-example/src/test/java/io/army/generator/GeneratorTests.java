package io.army.generator;

import io.army.SessionFactory;
import io.army.boot.BootstrapTests;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GeneratorTests {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorTests.class);

    @Test
    public void generatorMapping() {
        final long startTime = System.currentTimeMillis();
        Map<String, Object> map = new HashMap<>();

        map.put(SessionFactory.PACKAGE_TO_SCAN, "com.example.generator");

        SessionFactory sessionFactory = BootstrapTests.builder(map)
                .build();
        LOG.info("table generator chain:");

        for (Map.Entry<TableMeta<?>, List<FieldMeta<?, ?>>> e : sessionFactory.tableGeneratorChain().entrySet()) {
            StringBuilder builder = new StringBuilder("table:");
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
        LOG.info("cost {} ms", System.currentTimeMillis() - startTime);
    }
}
