module army.spring {
    requires jsr305;
    requires org.slf4j;

    requires transitive army.sync;
    requires transitive army.reactive;

    exports io.army.spring;
    exports io.army.spring.sync;
    exports io.army.spring.reactive;
}
