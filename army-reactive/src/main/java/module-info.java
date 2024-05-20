module army.reactive {

    requires jsr305;
    requires org.slf4j;
    requires transitive army.core;

    exports io.army.reactive;
    exports io.army.reactive.executor;
}
