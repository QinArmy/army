package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.*;
import io.army.criteria.MetaException;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.AnnotationUtils;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;
import org.springframework.lang.Nullable;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

abstract class MetaUtils {

    private static final Map<String, Map<Integer, String>> DISCRIMINATOR_MAP = new HashMap<>();

    private static final Set<String> WITHOUT_DEFAULT_TYPE_NAMES = createWithoutDefaultTypeNameSet();

    private static Set<String> createWithoutDefaultTypeNameSet() {
        Set<String> set = new HashSet<>(MetaConstant.WITHOUT_DEFAULT_TYPES.size() + 4);
        for (Class<?> clazz : MetaConstant.WITHOUT_DEFAULT_TYPES) {
            set.add(clazz.getName());
        }
        return Collections.unmodifiableSet(set);
    }


    static void throwInheritanceDuplication(TypeElement entityElement) throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,
                "Entity[%s] extends link %s count great than 1 in link of extends",
                entityElement.getQualifiedName(),
                Inheritance.class.getName());
    }

    static void throwMultiLevelInheritance(TypeElement entityElement) throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,
                "Entity[%s] inheritance level greater than 2,it's parentMeta's MappingMode is Child.",
                entityElement.getQualifiedName());
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
            // throw new MetaException(ErrorCode.META_ERROR,String.format("entityMappedElementList:%s",entityElement));
            throw createMissingPropException(entityElement, missPropNameSet);
        }
    }

    static void assertMappingModeParent(List<TypeElement> entityMappedElementList
            , List<TypeElement> parentMappedElementList) {
        TypeElement parentEntity = SourceCreateUtils.entityElement(parentMappedElementList);
        Inheritance inheritance = null;
        if (parentEntity != null) {
            inheritance = parentEntity.getAnnotation(Inheritance.class);
        }

        if (inheritance != null) {
            assertMappingChild(entityMappedElementList, parentEntity.getQualifiedName().toString());
        } else {
            TypeElement entityElement = SourceCreateUtils.entityElement(entityMappedElementList);
            Assert.notNull(entityElement, "entityMappedElementList error ");
            inheritance = entityElement.getAnnotation(Inheritance.class);
            if (inheritance == null) {
                assertMappingSimple(entityElement);
            } else {
                assertMappingParent(entityElement);
            }

        }
    }

    private static void assertMappingSimple(TypeElement entityElement) {

        if (entityElement.getAnnotation(DiscriminatorValue.class) != null) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] couldn'field have %s"
                    , entityElement.getQualifiedName()
                    , DiscriminatorValue.class.getName());
        }

    }


    static void assertColumn(TypeElement entityElement, VariableElement mappedProp, Column column
            , String columnName, @Nullable String discriminatorColumn) {
        final String propName = mappedProp.getSimpleName().toString();

        // check nullable
        if (TableMeta.RESERVED_PROPS.contains(propName)
                || (discriminatorColumn != null && !columnName.equals(discriminatorColumn))) {

            if (column.nullable()) {
                throw new MetaException(ErrorCode.META_ERROR, "Domain[%s] column[%s] nullable must be false.",
                        entityElement.getQualifiedName(),
                        columnName
                );
            }

        }

        if (!TableMeta.RESERVED_PROPS.contains(propName)
                && !columnName.equals(discriminatorColumn)) {
            // check comment
            if (!StringUtils.hasText(column.comment())) {
                throw new MetaException(ErrorCode.META_ERROR, "Domain[%s] column[%s] no comment.",
                        entityElement.getQualifiedName(),
                        columnName
                );
            }
            if (!isSpecifyTypeWithoutDefaultValue(mappedProp)
                    && !StringUtils.hasText(column.defaultValue())) {
                throw new MetaException(ErrorCode.META_ERROR, "Domain[%s] column[%s] no defaultValue.",
                        entityElement.getQualifiedName(),
                        columnName
                );
            }

        }
        // assert @Mapping annotation
        assertMapping(entityElement, mappedProp);
    }


    static void throwDiscriminatorNotEnum(TypeElement entityElement, VariableElement mappedProp)
            throws MetaException {
        throw new MetaException(ErrorCode.META_ERROR,
                "entity[%s] discriminator property[%s] isn'field a Enum that implements %s .",
                entityElement.getQualifiedName(),
                mappedProp.getSimpleName(),
                CodeEnum.class.getName()
        );
    }

    static void assertMappingPropNotDuplication(TypeElement mappedElement, String propName, Set<String> propNameSet) {
        if (propNameSet.contains(propName)) {
            // child class duplicate mapping prop allowed by army
            throw new MetaException(ErrorCode.META_ERROR, String.format(
                    "Mapped class[%s] mapping property[%s] duplication"
                    , mappedElement.getQualifiedName(), propName));
        } else {
            propNameSet.add(propName);
        }
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
            throw new MetaException(ErrorCode.META_ERROR, "entity[%s] index map %s not exits.",
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

    static void assertReservedColumnName(TypeElement entityElement, VariableElement mappedProp, Column column) {
        if (TableMeta.RESERVED_PROPS.contains(mappedProp.getSimpleName().toString())
                && StringUtils.hasText(column.name())) {
            throw new MetaException(ErrorCode.META_ERROR,
                    "domain[%s] reserved prop[%s] column name must use default value .",
                    entityElement.getQualifiedName(),
                    mappedProp.getSimpleName()
            );
        }
    }

    static boolean isCodeEnum(VariableElement mappingPropElement) {
        TypeMirror typeMirror = mappingPropElement.asType();

        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }

        Element element = ((DeclaredType) typeMirror).asElement();
        if (element.getKind() != ElementKind.ENUM) {
            return false;
        }
        TypeElement enumElement = (TypeElement) element;

        boolean match = false;
        for (TypeMirror enumInterface : enumElement.getInterfaces()) {
            if (enumInterface.toString().equals(CodeEnum.class.getName())) {
                match = true;
                break;
            }
        }
        return match;
    }

    static boolean isReservedProp(VariableElement mappingPropElement) {
        return TableMeta.RESERVED_PROPS.contains(mappingPropElement.getSimpleName().toString())
                ;
    }

    /*################################## blow private method ##################################*/

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

    private static boolean isSpecifyTypeWithoutDefaultValue(VariableElement mappedProp) {
        String className = mappedProp.asType().toString();
        return WITHOUT_DEFAULT_TYPE_NAMES.contains(className)
                || isCodeEnum(mappedProp)
                ;
    }

    private static void assertMapping(TypeElement entityElement, VariableElement mappedProp) {
        Mapping mapping = mappedProp.getAnnotation(Mapping.class);
        if (mapping == null) {
            return;
        }
        if (mapping.mapping() == AnnotationUtils.getDefaultValue(Mapping.class, "mapping")
                && !StringUtils.hasText(mapping.value())) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] mapping prop[%s] Mapping no value",
                    entityElement.getQualifiedName(),
                    mappedProp.getSimpleName());
        }
    }


    private static void assertMappingChild(List<TypeElement> entityMappedElementList, String parentClassName) {
        TypeElement childEntity = SourceCreateUtils.entityElement(entityMappedElementList);
        Assert.notNull(childEntity, "entityMappedElementList error ");
        DiscriminatorValue discriminatorValue = childEntity.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            throw new MetaException(ErrorCode.META_ERROR, "Entity[%s] no %s"
                    , childEntity.getQualifiedName()
                    , DiscriminatorValue.class.getName());
        }

        Map<Integer, String> codeMap = DISCRIMINATOR_MAP.computeIfAbsent(parentClassName, key -> new HashMap<>());
        String actualClassName = codeMap.get(discriminatorValue.value());
        String className = childEntity.getQualifiedName().toString();
        if (actualClassName != null && !actualClassName.equals(className)) {
            throw new MetaException(ErrorCode.META_ERROR, "child Entity[%s] discriminatorValue duplication", className);
        } else {
            codeMap.putIfAbsent(discriminatorValue.value(), className);
        }
    }

    private static void assertMappingParent(TypeElement parentEntity) {
        String className = parentEntity.getQualifiedName().toString();
        DiscriminatorValue discriminatorValue = parentEntity.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null && discriminatorValue.value() != 0) {
            throw new MetaException(ErrorCode.META_ERROR, "parentMeta Entity[%s] discriminatorValue must be 0", className);
        }

        Map<Integer, String> codeMap = DISCRIMINATOR_MAP.computeIfAbsent(className, key -> new HashMap<>());
        String actualClassName = codeMap.get(0);
        if (actualClassName != null && !actualClassName.equals(className)) {
            throw new MetaException(ErrorCode.META_ERROR, "parentMeta Entity[%s] discriminatorValue duplication", className);
        } else {
            codeMap.putIfAbsent(0, className);
        }
    }

}
