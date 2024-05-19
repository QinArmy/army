module army.core {
    requires jsr305;
    requires org.slf4j;
    requires transitive army.annotation;
    requires transitive army.struct;

    exports io.army;

    exports io.army.advice;
    exports io.army.bean;
    exports io.army.codec;
    exports io.army.criteria;

    exports io.army.criteria.standard;
    exports io.army.criteria.dialect;
    exports io.army.criteria.impl.inner to army.postgre;

    exports io.army.datasource;
    exports io.army.dialect;
    exports io.army.dialect.postgre;
    exports io.army.env;

    exports io.army.executor;
    exports io.army.function;
    exports io.army.generator;
    exports io.army.generator.snowflake;

    exports io.army.mapping;
    exports io.army.mapping.array;
    exports io.army.mapping.mysql;
    exports io.army.mapping.optional;

    exports io.army.mapping.postgre;
    exports io.army.mapping.spatial;
    exports io.army.mapping.sqlite;
    exports io.army.meta;

    exports io.army.proxy;
    exports io.army.schema;
    exports io.army.session;
    exports io.army.record;

    exports io.army.sqltype;
    exports io.army.stmt;
    exports io.army.type;

    exports io.army.util;
    exports io.army.criteria.impl;


}
