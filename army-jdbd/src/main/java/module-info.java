module army.jdbd {

    requires jsr305;
    requires org.slf4j;
    requires reactor.core;
    requires transitive army.reactive;

    requires jdbd.spi;

    exports io.army.jdbd;
}
