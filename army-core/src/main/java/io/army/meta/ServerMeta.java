package io.army.meta;

import io.army.dialect.Database;
import io.army.dialect.Dialect;

import javax.annotation.Nullable;

public interface ServerMeta {

    String name();

    /**
     * <p>
     * This method is equivalent to {@link Dialect#database()} of {@link #usedDialect()}
     * </p>
     *
     * @see #serverDatabase()
     */
    Database dialectDatabase();

    /**
     * @see #dialectDatabase()
     */
    Database serverDatabase();

    @Nullable
    String catalog();

    @Nullable
    String schema();

    String version();

    int major();

    int minor();

    /**
     * from {@link io.army.env.ArmyKey#DIALECT}
     */
    Dialect usedDialect();


    boolean meetsMinimum(int major, int minor, int subMinor);

    boolean isSupportSavePoints();

    static Builder builder() {
        return ServerMetaImpl.builder();
    }

    interface Builder {

        Builder name(String name);

        Builder catalog(String catalogName);

        Builder schema(String schemaName);

        Builder database(Database database);

        Builder version(String version);

        Builder major(int major);

        Builder minor(int minor);

        Builder subMinor(int subMinor);

        Builder usedDialect(Dialect dialect);

        Builder supportSavePoint(boolean support);

        ServerMeta build();

    }


}
