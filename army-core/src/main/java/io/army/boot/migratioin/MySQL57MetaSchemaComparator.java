package io.army.boot.migratioin;

import io.army.criteria.MetaException;
import io.army.dialect.Database;
import io.army.meta.FieldMeta;
import io.army.schema.SchemaInfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MySQL57MetaSchemaComparator extends AbstractMetaSchemaComparator {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    MySQL57MetaSchemaComparator() {
    }

    @Override
    protected boolean precisionOrScaleAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {

        throw new UnsupportedOperationException();
    }

    protected Database database() {
        return Database.MySQL57;
    }

    @Override
    protected boolean defaultValueAlter(FieldMeta<?, ?> fieldMeta, ColumnInfo columnInfo)
            throws SchemaInfoException, MetaException {
        //TODO zoro implement interpreter model
        return false;
    }

}
