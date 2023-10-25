package io.army.reactive.executor;

import io.army.reactive.ReactiveCloseable;
import reactor.core.publisher.Mono;


public interface StmtExecutorFactory extends ReactiveCloseable {

    /**
     * <p>For example: io.jdbd
     *
     * @return driver spi vendor,The value returned typically is the package name for this vendor.
     */
    String driverSpiVendor();

    Mono<MetaExecutor> metaExecutor();

    Mono<LocalStmtExecutor> localStmtExecutor();

    Mono<RmStmtExecutor> rmStmtExecutor();

}
