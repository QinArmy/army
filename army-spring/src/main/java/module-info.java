module army.spring {

    requires jsr305;
    requires org.slf4j;
    requires druid;
    requires com.zaxxer.hikari;

    requires spring.core;
    requires spring.jdbc;
    requires spring.tx;
    requires spring.beans;
    requires spring.context;

    requires transitive army.sync;
    requires transitive army.reactive;

    exports io.army.spring;
    exports io.army.spring.sync;
    exports io.army.spring.reactive;
}
