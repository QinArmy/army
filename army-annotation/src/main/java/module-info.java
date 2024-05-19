module army.annotation {
    requires jsr305;
    requires java.compiler;
    requires transitive army.struct;

    exports io.army.annotation;
    exports io.army.modelgen;

}
