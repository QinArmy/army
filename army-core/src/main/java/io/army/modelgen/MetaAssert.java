package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;

import javax.annotation.Nullable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

abstract class MetaAssert {


    static void throwInheritanceDuplication(TypeElement entityElement) throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,
                "Entity[%s] extends link %s count great than 1 in link of extends",
                entityElement.getQualifiedName(),
                Inheritance.class.getName());
    }


    /**
     * <ul>
     *     <li>if entity {@link Table#immutable()} true ,entityPropNameSet must contains
     *     {@link TableMeta#DOMAIN_PROPS}</li>
     *      <li>if entity {@link Table#immutable()} false ,entityPropNameSet must contains
     *      {@link TableMeta#VERSION_PROPS}</li>
     * </ul>
     */
    static void assertRequiredMappingProps(List<TypeElement> entityMappedElementList,
                                           Set<String> entityPropNameSet) throws MetaException {

        TypeElement entityElement = entityMappedElementList.get(entityMappedElementList.size() - 1);
        Table table = entityElement.getAnnotation(Table.class);

        Assert.notNull(table, () -> String.format(
                "entity[%s] entityMappedElementList  error", entityElement.getQualifiedName()));

        Set<String> missPropNameSet;
        if (table.immutable()) {
            missPropNameSet = createMissingPropNameSet(entityPropNameSet, TableMeta.DOMAIN_PROPS);
        } else {
            missPropNameSet = createMissingPropNameSet(entityPropNameSet, TableMeta.VERSION_PROPS);
        }
        if (!CollectionUtils.isEmpty(missPropNameSet)) {
            throw createMissingPropException(entityElement, missPropNameSet);
        }
    }

    static void assertColumn(TypeElement mappedElement, VariableElement mappedProp, Column column,
                             String columnName) {
        if (TableMeta.VERSION_PROPS.contains(mappedProp.getSimpleName().toString())) {
            return;
        }
        if (!StringUtils.hasText(column.comment())) {
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] no comment.",
                    mappedElement.getQualifiedName(),
                    columnName
            );
        }
        if (!StringUtils.hasText(column.defaultValue())) {
            throw new MetaException(ErrorCode.META_ERROR, "mapped class[%s] column[%s] no defaultValue.",
                    mappedElement.getQualifiedName(),
                    columnName
            );
        }


    }

    static MetaException cratePropertyDuplication(TypeElement mappedElement, VariableElement mappedField) {
        return new MetaException(ErrorCode.META_ERROR, String.format(
                "Mapped class[%s] mapping property[%s] duplication"
                , mappedElement.getQualifiedName(), mappedField.getSimpleName()));
    }

    static void throwDiscriminatorNotEnum(TypeElement entityElement, VariableElement mappedProp)
            throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,
                "entity[%s] discriminator property[%s] isn't a Enum that implements %s .",
                entityElement.getQualifiedName(),
                mappedProp.getSimpleName(),
                CodeEnum.class.getName()
        );
    }

    static void assertIndexColumnNameSet(@Nullable TypeElement entityElement, Set<String> columnNameSet,
                                         Set<String> indexColumnNameSet)
            throws MetaException {
        if (entityElement == null) {
            return;
        }

        Set<String> missingColumnNameSet = new HashSet<>();
        for (String columnName : indexColumnNameSet) {
            if (!columnNameSet.contains(columnName)) {
                missingColumnNameSet.add(columnName);
            }
        }
        if (!missingColumnNameSet.isEmpty()) {
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] index %s not exits.",
                    entityElement.getQualifiedName(),
                    missingColumnNameSet
            );
        }
    }

    static void assertInheritance(@Nullable TypeElement entityElement, List<String> discriminatorColumnList) {
        if (entityElement != null && entityElement.getAnnotation(Inheritance.class) != null) {
            Assert.notEmpty(discriminatorColumnList,
                    () -> String.format("entity[%s] discriminator column not exists.",
                            entityElement.getQualifiedName()));
        }
    }

    static MetaException createColumnDuplication(TypeElement mappedElement, String columnName) {
        return new MetaException(ErrorCode.META_ERROR, String.format(
                "Mapped class[%s] mapping column[%s] duplication"
                , mappedElement.getQualifiedName(), columnName));
    }

    static void assertRequiredColumnName(TypeElement entityElement, VariableElement mappedProp, Column column) {
        if (TableMeta.VERSION_PROPS.contains(mappedProp.getSimpleName().toString())
                && StringUtils.hasText(column.name())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "entity[%s] required prop[%s] column name must use default value .",
                    entityElement.getQualifiedName(),
                    mappedProp.getSimpleName()
            );
        }
    }

    /*################################## private method #########################################*/

    private static Set<String> createMissingPropNameSet(Set<String> entityPropNameSet,
                                                        Set<String> requiredPropNameSet) {
        Set<String> missPropNameSet = new HashSet<>(8);
        for (String requiredPropName : requiredPropNameSet) {
            if (!entityPropNameSet.contains(requiredPropName)) {
                missPropNameSet.add(requiredPropName);
            }
        }
        return Collections.unmodifiableSet(missPropNameSet);
    }

    private static MetaException createMissingPropException(TypeElement entityElement,
                                                            Set<String> missPropNameSet) {
        return new MetaException(ErrorCode.META_ERROR, "Entity[%s] missing required mapping properties %s",
                entityElement.getQualifiedName(),
                missPropNameSet);
    }


}