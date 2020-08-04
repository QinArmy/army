package io.army.boot.migratioin;

import io.army.GenericRmSessionFactory;

import java.sql.Connection;

public interface SyncMetaMigrator {

    void migrate(Connection conn, GenericRmSessionFactory sessionFactory);

    static SyncMetaMigrator build() {
        return new SyncMetaMigratorImpl();
    }
}
