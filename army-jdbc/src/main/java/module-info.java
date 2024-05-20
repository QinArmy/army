module army.jdbc {
    requires jsr305;
    requires org.slf4j;
    requires transitive java.sql;
    requires transitive java.transaction.xa;
    requires org.postgresql.jdbc;


    requires transitive army.sync;

    exports io.army.jdbc;
}
