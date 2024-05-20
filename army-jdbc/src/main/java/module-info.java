module army.jdbc {
    requires jsr305;
    requires org.slf4j;
    requires transitive army.sync;

    exports io.army.jdbc;
}
