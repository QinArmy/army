module army.sqlite {
    requires jsr305;
    requires transitive army.core;

    exports io.army.criteria.sqlite;
    exports io.army.mapping.sqlite;
}
