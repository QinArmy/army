package io.army.boot.migratioin;

import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.meta.MetaException;
import io.army.schema.SchemaInfoException;
import io.army.session.DialectSessionFactory;
import io.army.sqltype.PostgreType;

import java.util.List;
import java.util.Map;
import java.util.Set;

class Postgre11MetaSchemaComparator extends AbstractMetaSchemaComparator {

    static final Set<PostgreType> INTEGER_TYPE_SET = PostgreUtils.createIntegerSet();

    static final Set<PostgreType> FLOAT_TYPE_SET = PostgreUtils.createFloatSet();

    static final Set<PostgreType> EXACT_NUMERIC_TYPE_SET = PostgreUtils.createExactNumericSet();

    static final Set<PostgreType> NUMERIC_TYPE_SET = PostgreUtils.createNumericSet();

    static final Map<PostgreType, List<String>> SYNONYMS_MAP = PostgreUtils.createSynonymsMap();

    Postgre11MetaSchemaComparator(DialectSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected boolean needModifyPrecisionOrScale(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        return false;
    }

    @Override
    protected boolean needModifyDefault(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        return false;
    }

    @Override
    protected boolean synonyms(FieldMeta<?, ?> fieldMeta, String sqlTypeName) {
//        String upperCaseTypName = sqlTypeName.toUpperCase();
//        SqlDataType fieldDataType = fieldMeta.mappingMeta().sqlDataType(database());
//        boolean match = fieldDataType.typeName().equals(upperCaseTypName);
//        if (!match && fieldDataType instanceof PostgreDataType) {
//            List<String> synonymsList = SYNONYMS_MAP.get(fieldDataType);
//            match = synonymsList != null && synonymsList.contains(upperCaseTypName);
//        }
        return false;
    }

    @Override
    protected Database database() {
        return Database.PostgreSQL;
    }
}
