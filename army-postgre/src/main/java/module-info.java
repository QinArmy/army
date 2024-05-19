module army.postgre {
    requires jsr305;
    requires transitive army.core;

    exports io.army.criteria.postgre;
    exports io.army.dialect.postgre;
}
