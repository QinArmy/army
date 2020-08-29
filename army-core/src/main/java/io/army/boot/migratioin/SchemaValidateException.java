package io.army.boot.migratioin;

import io.army.SessionFactoryException;

import java.util.List;

final class SchemaValidateException extends SessionFactoryException {

    private final List<List<Migration>> migrationGroupList;


    SchemaValidateException(List<List<Migration>> migrationGroupList, String format) {
        super(format);
        this.migrationGroupList = migrationGroupList;
    }

    public List<List<Migration>> getMigrationGroupList() {
        return this.migrationGroupList;
    }
}
