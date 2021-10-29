package io.army.sync.executor;

public interface ExecutorProvider {

    ExecutorFactory createFactory(Object dataSource);

}
