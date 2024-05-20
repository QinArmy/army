module army.mysql {
    requires jsr305;
    requires transitive army.core;

    exports io.army.criteria.mysql;
    exports io.army.mapping.mysql;
}
