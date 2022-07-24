package io.army.criteria.impl;

import io.army.annotation.GeneratorType;
import io.army.annotation.UpdateMode;
import io.army.criteria.QualifiedField;
import io.army.criteria.TableField;
import io.army.criteria.Visible;
import io.army.criteria.impl.inner._Selection;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;
import io.army.modelgen._MetaBridge;
import io.army.util._Exceptions;

import java.lang.ref.SoftReference;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


final class QualifiedFieldImpl<T> extends OperationField
        implements QualifiedField<T>, _Selection {

    @SuppressWarnings("unchecked")
    static <T> QualifiedField<T> reference(final String tableAlias, final FieldMeta<T> field) {
        final ConcurrentMap<String, FieldReference<?>> fieldMap;
        fieldMap = CACHE.computeIfAbsent(field, QualifiedFieldImpl::createFieldReferenceMap);

        final FieldReference<?> fieldReference;
        fieldReference = fieldMap.computeIfAbsent(tableAlias, k -> {
            final FieldReference<?> reference;
            reference = new FieldReference<>(new QualifiedFieldImpl<>(tableAlias, field), fieldMap);
            return reference;
        });

        QualifiedFieldImpl<?> qualifiedField;
        qualifiedField = fieldReference.get();

        if (qualifiedField == null
                || qualifiedField.field != field
                || qualifiedField.tableAlias.equals(tableAlias)) {
            qualifiedField = new QualifiedFieldImpl<>(tableAlias, field);
            fieldMap.put(tableAlias, new FieldReference<>(qualifiedField, fieldMap));
        }
        return (QualifiedField<T>) qualifiedField;
    }


    private static ConcurrentMap<String, FieldReference<?>> createFieldReferenceMap(FieldMeta<?> field) {
        return new ConcurrentHashMap<>();
    }

    private static final ConcurrentMap<FieldMeta<?>, ConcurrentMap<String, FieldReference<?>>>
            CACHE = new ConcurrentHashMap<>();

    private final String tableAlias;

    private final DefaultFieldMeta<T> field;

    private QualifiedFieldImpl(final String tableAlias, final FieldMeta<T> field) {
        this.field = (DefaultFieldMeta<T>) field;
        this.tableAlias = tableAlias;
    }

    @Override
    public UpdateMode updateMode() {
        return this.field.updateMode;
    }

    @Override
    public boolean codec() {
        return this.field.codec();
    }

    @Override
    public boolean nullable() {
        return this.field.nullable;
    }

    @Override
    public ParamMeta paramMeta() {
        return this.field;
    }

    @Override
    public TableField tableField() {
        // return this
        return this;
    }

    @Override
    public void appendSelection(final _SqlContext context) {
        context.appendField(this.tableAlias, this.field);

        context.sqlBuilder()
                .append(_Constant.SPACE_AS_SPACE)
                .append(this.field.fieldName);
    }

    @Override
    public void appendSql(final _SqlContext context) {
        if (context.visible() != Visible.BOTH && _MetaBridge.VISIBLE.equals(this.field.fieldName())) {
            throw _Exceptions.visibleField(context.visible(), this);
        }
        context.appendField(this.tableAlias, this.field);
    }

    @Override
    public String alias() {
        return this.field.fieldName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.tableAlias, this.field);
    }

    @Override
    public boolean equals(final Object obj) {
        final boolean match;
        if (obj == this) {
            match = true;
        } else if (obj instanceof QualifiedFieldImpl) {
            final QualifiedFieldImpl<?> o = (QualifiedFieldImpl<?>) obj;
            match = o.field == this.field && o.tableAlias.equals(this.tableAlias);
        } else {
            match = false;
        }
        return match;
    }

    @Override
    public String toString() {
        return String.format(" %s.%s", this.tableAlias, this.field);
    }

    @Override
    public String tableAlias() {
        return this.tableAlias;
    }

    @Override
    public FieldMeta<T> fieldMeta() {
        return this.field;
    }

    @Override
    public TableMeta<T> tableMeta() {
        return this.field.table;
    }

    @Override
    public Class<?> javaType() {
        return this.field.javaType;
    }

    @Override
    public String fieldName() {
        return this.field.fieldName;
    }

    @Override
    public String columnName() {
        return this.field.columnName;
    }

    @Override
    public String objectName() {
        return this.field.columnName;
    }

    @Override
    public MappingType mappingType() {
        return this.field.mappingType;
    }

    @Override
    public boolean insertable() {
        return this.field.insertable;
    }

    @Override
    public GeneratorType generatorType() {
        return this.field.generatorType;
    }

    private static final class FieldReference<T> extends SoftReference<QualifiedFieldImpl<T>> {


        private final ConcurrentMap<String, FieldReference<?>> aliasToRef;

        private FieldReference(QualifiedFieldImpl<T> referent, ConcurrentMap<String, FieldReference<?>> aliasToRef) {
            super(referent);
            this.aliasToRef = aliasToRef;
        }

        @Override
        public void clear() {
            final QualifiedFieldImpl<T> referent;
            referent = this.get();
            super.clear();
            if (referent != null) {
                this.aliasToRef.remove(referent.tableAlias(), this);
                if (this.aliasToRef.size() == 0) {
                    CACHE.remove(referent.fieldMeta());
                }
            }
        }


    }// FieldReference


}
