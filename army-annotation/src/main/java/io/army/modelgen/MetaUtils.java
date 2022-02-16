package io.army.modelgen;

import io.army.annotation.*;
import io.army.lang.Nullable;
import io.army.struct.CodeEnum;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.*;

abstract class MetaUtils {


    static final String CODE_ENUM = "io.army.struct.CodeEnum";

    static final String CODE_ENUM_TYPE = "io.army.mapping.CodeEnumType";

    static final String NAME_ENUM_TYPE = "io.army.mapping.NameEnumType";

    private static final Map<String, Map<Integer, String>> DISCRIMINATOR_MAP = new HashMap<>();

    private static final Set<String> WITHOUT_DEFAULT_TYPE_NAMES = createWithoutDefaultTypeNameSet();

    private static Set<String> createWithoutDefaultTypeNameSet() {
        Set<String> set = new HashSet<>(_MetaBridge.WITHOUT_DEFAULT_TYPES.size() + 6);
        for (Class<?> clazz : _MetaBridge.WITHOUT_DEFAULT_TYPES) {
            set.add(clazz.getName());
        }
        set.add("org.reactivestreams.Publisher");
        return CollectionUtils.asUnmodifiableSet(set);
    }


    static void assertRequiredProps(TypeElement domainElement, Set<String> entityPropNameSet
            , final boolean tableImmutable) throws AnnotationMetaException {

        if (domainElement.getAnnotation(Inheritance.class) != null
                || domainElement.getAnnotation(DiscriminatorValue.class) == null) {
            // parent or simple table
            final Set<String> missingProps;
            if (tableImmutable) {
                missingProps = MetaUtils.createMissingPropNameSet(entityPropNameSet, _MetaBridge.DOMAIN_PROPS);
            } else {
                missingProps = MetaUtils.createMissingPropNameSet(entityPropNameSet, _MetaBridge.UPDATE_PROPS);
            }
            if (!missingProps.isEmpty()) {
                throw Exceptions.missingProps(domainElement, missingProps);
            }
        }

    }


    static void assertColumn(TypeElement entityElement, VariableElement mappedProp, Column column
            , String columnName, @Nullable String discriminatorField) {
        final String propName = mappedProp.getSimpleName().toString();
        final boolean reservedProp = _MetaBridge.RESERVED_PROPS.contains(propName);
        // check nullable
        // check comment
        final String fieldName = mappedProp.getSimpleName().toString();
        if (!reservedProp && !fieldName.equals(discriminatorField) && !Strings.hasText(column.comment())) {
            String m;
            m = String.format("Domain[%s] column[%s] no comment,non-reserved(or discriminator) property must have comment.",
                    entityElement.getQualifiedName(),
                    fieldName);
            throw new AnnotationMetaException(m);
        }
        if (!column.nullable() && !isSpecifyTypeWithoutDefaultValue(mappedProp) && !Strings.hasText(column.defaultValue())) {
            String m = String.format("Domain[%s] column[%s] no defaultValue.",
                    entityElement.getQualifiedName(),
                    columnName);
            throw new AnnotationMetaException(m);
        }
        // assert @Mapping annotation
        final Mapping mapping = mappedProp.getAnnotation(Mapping.class);
        if (mapping == null) {
            return;
        }
        final String mappingValue = mapping.value();
        if (mappingValue.isEmpty() && !Strings.hasText(mappingValue)) {
            String m = String.format("Domain[%s] mapping property[%s] Mapping no value",
                    entityElement.getQualifiedName(),
                    mappedProp.getSimpleName());
            throw new AnnotationMetaException(m);
        }
        if (getEnumElement(mappedProp) != null) {
            if (isCodeEnum(mappedProp) && !mappingValue.equals(CODE_ENUM)) {
                String m = String.format("Domain[%s] mapping property[%s] is %s ,so only mapping %s"
                        , entityElement.getQualifiedName()
                        , mappedProp.getSimpleName()
                        , CODE_ENUM
                        , CODE_ENUM_TYPE);
                throw new AnnotationMetaException(m);
            }
            if (!mappingValue.equals(NAME_ENUM_TYPE)) {
                String m = String.format("Domain[%s] mapping property[%s] isn't %s ,so only mapping %s"
                        , entityElement.getQualifiedName()
                        , mappedProp.getSimpleName()
                        , CODE_ENUM
                        , NAME_ENUM_TYPE);
                throw new AnnotationMetaException(m);
            }

        }


    }


    static void assertIndexFieldNameSet(TypeElement entityElement, Set<String> fieldNameSet,
                                        Set<String> indexFieldNameSet)
            throws AnnotationMetaException {

        Set<String> missingColumnNameSet = null;
        for (String fieldName : indexFieldNameSet) {
            if (!fieldNameSet.contains(fieldName)) {
                if (missingColumnNameSet == null) {
                    missingColumnNameSet = new HashSet<>();
                }
                missingColumnNameSet.add(fieldName);
            }
        }
        if (missingColumnNameSet != null) {
            String m = String.format("Domain[%s] index map %s not exits.",
                    entityElement.getQualifiedName(),
                    missingColumnNameSet);
            throw new AnnotationMetaException(m);
        }

    }


    static boolean isCodeEnum(VariableElement propElement) {
        final TypeElement enumElement;
        enumElement = getEnumElement(propElement);
        boolean match = false;
        if (enumElement != null) {
            final String codeEnumClassName = CodeEnum.class.getName();
            for (TypeMirror enumInterface : enumElement.getInterfaces()) {
                if (codeEnumClassName.equals(enumInterface.toString())) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    @Nullable
    static TypeElement getEnumElement(final VariableElement propElement) {
        final TypeMirror typeMirror = propElement.asType();

        final TypeElement enumElement;
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            final Element element = ((DeclaredType) typeMirror).asElement();
            if (element.getKind() == ElementKind.ENUM) {
                enumElement = (TypeElement) element;
            } else {
                enumElement = null;
            }
        } else {
            enumElement = null;
        }
        return enumElement;
    }

    static boolean isReservedProp(VariableElement mappingPropElement) {
        return _MetaBridge.RESERVED_PROPS.contains(mappingPropElement.getSimpleName().toString());
    }

    /*################################## blow private method ##################################*/

    private static Set<String> createMissingPropNameSet(Set<String> entityPropNameSet,
                                                        Collection<String> requiredPropNameSet) {
        final Set<String> missPropNameSet = new HashSet<>(8);
        for (String requiredPropName : requiredPropNameSet) {
            if (!entityPropNameSet.contains(requiredPropName)) {
                missPropNameSet.add(requiredPropName);
            }
        }
        return missPropNameSet.isEmpty() ? Collections.emptySet() : missPropNameSet;
    }


    private static boolean isSpecifyTypeWithoutDefaultValue(VariableElement mappedProp) {
        return WITHOUT_DEFAULT_TYPE_NAMES.contains(mappedProp.asType().toString())
                || getEnumElement(mappedProp) != null;
    }

    static String domainClassName(TypeElement domainElement) {
        String className = domainElement.getQualifiedName().toString();
        final int index = className.indexOf('<');
        if (index > 0) {
            className = className.substring(0, index);
        }
        return className;
    }


    static void assertMappingChild(TypeElement childElement, TypeElement parentElement) {
        final DiscriminatorValue discriminatorValue = childElement.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue == null) {
            throw Exceptions.childNoDiscriminatorValueAnnotation(childElement);
        }
        final String parentClassName = domainClassName(parentElement);
        final Map<Integer, String> codeMap = DISCRIMINATOR_MAP.computeIfAbsent(parentClassName, key -> new HashMap<>());
        final String actualClassName = codeMap.get(discriminatorValue.value());
        final String childClassName = domainClassName(childElement);
        if (actualClassName != null && !actualClassName.equals(childClassName)) {
            throw Exceptions.discriminatorValueDuplication(childClassName);
        } else {
            codeMap.putIfAbsent(discriminatorValue.value(), childClassName);
        }
    }

    static void assertMappingParent(TypeElement parentElement) {
        final String className = parentElement.getQualifiedName().toString();
        final DiscriminatorValue discriminatorValue = parentElement.getAnnotation(DiscriminatorValue.class);
        if (discriminatorValue != null && discriminatorValue.value() != 0) {
            String m = String.format("parentMeta Entity[%s] discriminatorValue must be 0", className);
            throw new AnnotationMetaException(m);
        }

        final Map<Integer, String> codeMap = DISCRIMINATOR_MAP.computeIfAbsent(className, key -> new HashMap<>());
        final String actualClassName = codeMap.get(0);
        if (actualClassName != null && !actualClassName.equals(className)) {
            String m = String.format("Domain[%s] discriminatorValue duplication", className);
            throw new AnnotationMetaException(m);
        } else {
            codeMap.putIfAbsent(0, className);
        }

    }

    static String columnName(TypeElement entityElement, final String propName, Column column) {
        final String customColumnName = column.name();
        final String columnName;
        if (_MetaBridge.RESERVED_PROPS.contains(propName)) {
            columnName = _MetaBridge.camelToLowerCase(propName);
            if (!customColumnName.isEmpty() && !customColumnName.equals(columnName)) {
                String m = String.format("Domain[%s] reserved prop[%s] column name must use default value .",
                        entityElement.getQualifiedName(), propName);
                throw new AnnotationMetaException(m);
            }
        } else if (customColumnName.isEmpty()) {
            columnName = _MetaBridge.camelToLowerCase(propName);
        } else {
            columnName = _MetaBridge.camelToLowerCase(customColumnName);
        }
        return columnName;

    }


    /**
     * get entity element that annotated by {@link Table}
     */
    @Nullable
    static TypeElement domainElement(List<TypeElement> entityMappingElementList) {
        final TypeElement domainElement;
        if (CollectionUtils.isEmpty(entityMappingElementList)) {
            domainElement = null;
        } else {
            domainElement = entityMappingElementList.get(entityMappingElementList.size() - 1);
        }
        return domainElement;
    }


    static Map<String, IndexMode> createIndexFieldNameSet(final TypeElement domainElement, final boolean noParent) {
        final Table table = domainElement.getAnnotation(Table.class);
        final Index[] indexArray = table.indexes();
        final Map<String, IndexMode> indexMetaMap = new HashMap<>();

        final Set<String> indexNameSet = new HashSet<>(indexArray.length + 3);

        StringTokenizer tokenizer;
        for (Index index : indexArray) {
            // make index name lower case
            final String indexName = Strings.toLowerCase(index.name());
            if (indexNameSet.contains(indexName)) {
                String m = String.format("Domain[%s] index name[%s] duplication",
                        domainElement.getQualifiedName(), indexName);
                throw new AnnotationMetaException(m);
            }
            final IndexMode indexMode = IndexMode.resolve(index);
            indexNameSet.add(indexName);
            for (String fieldName : index.fieldList()) {
                tokenizer = new StringTokenizer(fieldName.trim(), " ", false);
                // make index field name lower case
                indexMetaMap.put(tokenizer.nextToken(), indexMode);
            }
        }
        if (noParent || domainElement.getAnnotation(Inheritance.class) != null) {
            indexMetaMap.put(_MetaBridge.ID, IndexMode.PRIMARY);
        }
        return Collections.unmodifiableMap(indexMetaMap);
    }


}
