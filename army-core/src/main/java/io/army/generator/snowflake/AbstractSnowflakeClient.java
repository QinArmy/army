package io.army.generator.snowflake;

import io.army.util._Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @see SnowflakeGenerator
 */
public abstract class AbstractSnowflakeClient implements SnowflakeClient {


    /*################################## blow protected static method ##################################*/

    protected final void updateDefaultGenerator(Worker worker) {
        SnowflakeGenerator.updateDefaultSnowflake();
    }

    /*################################## blow instance protect method ##################################*/

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected final ConcurrentMap<SnowflakeGenerator, Boolean> generatorHolder = new ConcurrentHashMap<>();

    protected final AtomicReference<Worker> workerHolder = new AtomicReference<>(null);

    protected final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, schedulerName());
        thread.setDaemon(true);
        return thread;
    });


    protected final void updateGenerator(SnowflakeGenerator generator) {
        generator.updateSnowflake();
    }

    protected String schedulerName() {
        return "SnowflakeClient scheduler";
    }

    /**
     *
     */
    protected abstract void initImportanceParam();


    @Override
    public boolean registerGenerator(SnowflakeGenerator generator) throws SnowflakeWorkerException {
        _Assert.notNull(generator, "generator required");
        final Worker worker = workerHolder.get();

        boolean match ;
        if (SnowflakeGenerator.isMatchWorker(worker, generator.getSnowflake())) {
            if (generatorHolder.putIfAbsent(generator, Boolean.TRUE) != null) {
                LOG.debug("register generator[{}]", generator);
            } else {
                LOG.debug("generator[{}] had registered by other thread", generator);
            }
            match = true;
        } else {
            match = false;
        }
        return match;
    }


    @Override
    public Worker currentWorker() {
        Worker worker = workerHolder.get();
        _Assert.state(worker != null, "Worker not init.");
        return worker;
    }

}
