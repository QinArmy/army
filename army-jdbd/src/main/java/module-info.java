module army.jdbd {

    requires jsr305;
    requires org.slf4j;
    requires transitive army.sync;

    exports io.army.jdbd;
}
