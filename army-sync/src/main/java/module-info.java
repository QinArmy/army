module army.sync {
    requires jsr305;
    requires org.slf4j;
    requires transitive army.core;

    exports io.army.sync;
    exports io.army.sync.dao;
    exports io.army.sync.executor;
}
