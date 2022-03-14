package io.army.boot.migratioin;

import io.army.dialect.DDLSQLExecuteException;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;
import reactor.core.publisher.Mono;

final class ReactiveMigratorImpl implements ReactiveMigrator {
    @Override
    public Mono<Void> migrate(Object databaseSession, DialectSessionFactory sessionFactory) throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException {
        return null;
    }


    //
//    @Override
//    public Mono<Void> migrate(Object databaseSession, GenericRmSessionFactory sessionFactory)
//            throws SchemaExtractException, SchemaInfoException, MetaException, DDLSQLExecuteException {
//        if (!(databaseSession instanceof DatabaseSession)) {
//            return Mono.error(new IllegalArgumentException(String.format(
//                    "unsupported database session[%s]", databaseSession.getClass().getName())));
//        }
//        String key = String.format(ArmyConfigConstant.MIGRATION_MODE, sessionFactory.name());
//        final MigrationMode migrationMode = sessionFactory.environment().getProperty(key, MigrationMode.class
//                , MigrationMode.NONE);
//
//        if (migrationMode == MigrationMode.NONE) {
//            return Mono.empty();
//        }
//        final StatelessSession session = ((StatelessSession) databaseSession);
//        final ReactiveSchemaExtractor schemaExtractor = ReactiveSchemaExtractor.build(session);
//        //1. extract schema info from database
//        return schemaExtractor.extract(null)
//                // 2. table meta and schema info
//                .map(schemaInfo -> MetaSchemaComparator.build(sessionFactory).compare(schemaInfo))
//                .filter(list -> !list.isEmpty())
//                // 3. validate or migrate
//                .flatMap(migrationList -> validateOrMigrate(migrationList, session, sessionFactory, migrationMode))
//                ;
//    }
//
//    private Mono<Void> validateOrMigrate(List<List<Migration>> migrationList, StatelessSession session
//            , GenericRmSessionFactory sessionFactory, MigrationMode migrationMode) {
//        Mono<Void> mono;
//        switch (migrationMode) {
//            case UPDATE:
//                mono = update(migrationList, session, sessionFactory);
//                break;
//            case VALIDATE:
//                mono = validate(migrationList);
//                break;
//            default:
//                mono = Mono.error(new IllegalArgumentException("migrationMode error."));
//        }
//        return mono;
//
//    }
//
//    private Mono<Void> update(List<List<Migration>> migrationList, StatelessSession session
//            , GenericRmSessionFactory sessionFactory) {
//        // 1. parse ddl
//        List<Map<String, List<String>>> ddlList = MigratorUtils.createDdlForShardingList(
//                migrationList, sessionFactory.dialect());
//        return ReactiveDDLSQLExecutor.build()
//                //2. execute ddl
//                .executeDDL(sessionFactory.databaseIndex(), ddlList, session);
//    }
//
//    private Mono<Void> validate(List<List<Migration>> migrationList) {
//        if (migrationList.isEmpty()) {
//            return Mono.empty();
//        }
//        StringBuilder builder = new StringBuilder();
//        int index = 0;
//        for (List<Migration> shardingMigration : migrationList) {
//            builder.append("\n\ndatabase[")
//                    .append(index)
//                    .append("]:");
//            for (Migration migration : shardingMigration) {
//                if (migration instanceof Migration.TableMigration) {
//                    builder.append("\nmiss table[")
//                            .append(migration.actualTableName())
//                            .append("]");
//                } else if (migration instanceof Migration.MemberMigration) {
//                    appendMembers((Migration.MemberMigration) migration, builder);
//                } else {
//                    return Mono.error(new IllegalArgumentException(
//                            "unknown Migration " + migration.getClass().getName()));
//                }
//
//
//            }
//            index++;
//        }
//        return Mono.error(new SchemaValidateException(migrationList, builder.toString()));
//    }
//
//    private static void appendMembers(Migration.MemberMigration migration, StringBuilder builder) {
//        String actualTableName = migration.actualTableName();
//        if (!migration.columnsToAdd().isEmpty()) {
//            appendAddColumns(migration, actualTableName, builder);
//        } else if (!migration.columnsToChange().isEmpty()) {
//            appendChangeColumns(migration, actualTableName, builder);
//        } else if (!migration.indexesToAdd().isEmpty()) {
//            appendAddIndexes(migration, actualTableName, builder);
//        } else if (!migration.indexesToDrop().isEmpty()) {
//            appendDropIndexes(migration, actualTableName, builder);
//        } else if (!migration.indexesToAlter().isEmpty()) {
//            appendAlterIndexes(migration, actualTableName, builder);
//        }
//    }
//
//    private static void appendAddColumns(Migration.MemberMigration migration, String actualTableName
//            , StringBuilder builder) {
//        List<FieldMeta<?>> fieldMetaList = migration.columnsToAdd();
//        builder.append("\nmiss table[")
//                .append(actualTableName)
//                .append("] column(s):");
//        for (FieldMeta<?> fieldMeta : fieldMetaList) {
//            builder.append("\n  ")
//                    .append(fieldMeta.fieldName());
//
//        }
//    }
//
//    private static void appendChangeColumns(Migration.MemberMigration migration, String actualTableName
//            , StringBuilder builder) {
//        List<FieldMeta<?>> fieldMetaList = migration.columnsToChange();
//        builder.append("\nneed change table[")
//                .append(actualTableName)
//                .append("] column(s):");
//        for (FieldMeta<?> fieldMeta : fieldMetaList) {
//            builder.append("\n  ")
//                    .append(fieldMeta.fieldName());
//
//        }
//    }
//
//    private static void appendAddIndexes(Migration.MemberMigration migration, String actualTableName
//            , StringBuilder builder) {
//        List<IndexMeta<?>> indexMetaList = migration.indexesToAdd();
//        builder.append("\nneed add table[")
//                .append(actualTableName)
//                .append("] index(es):");
//        for (IndexMeta<?> indexMeta : indexMetaList) {
//            builder.append("\n  ")
//                    .append(indexMeta.name());
//
//        }
//    }
//
//    private static void appendDropIndexes(Migration.MemberMigration migration, String actualTableName
//            , StringBuilder builder) {
//        List<String> indexNameList = migration.indexesToDrop();
//        builder.append("\nneed drop table[")
//                .append(actualTableName)
//                .append("] index(es):");
//        for (String indexName : indexNameList) {
//            builder.append("\n  ")
//                    .append(indexName);
//
//        }
//    }
//
//    private static void appendAlterIndexes(Migration.MemberMigration migration, String actualTableName
//            , StringBuilder builder) {
//        List<IndexMeta<?>> indexMetaList = migration.indexesToAlter();
//        builder.append("\nneed alter table[")
//                .append(actualTableName)
//                .append("] index(es):");
//        for (IndexMeta<?> indexMeta : indexMetaList) {
//            builder.append("\n  ")
//                    .append(indexMeta.name());
//
//        }
//    }

}
