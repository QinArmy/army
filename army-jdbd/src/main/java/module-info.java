module army.jdbd {

    requires jsr305;
    requires org.slf4j;
    requires transitive army.reactive;

    exports io.army.jdbd;
}
