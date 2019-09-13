package io.army.modelgen;

import io.army.ErrorCode;
import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.criteria.MetaException;
import io.army.criteria.impl.DefaultTable;
import io.army.meta.FieldMeta;
import io.army.meta.IndexFieldMeta;
import io.army.meta.TableMeta;
import io.army.struct.CodeEnum;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.annotation.Generated;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.persistence.metamodel.StaticMetamodel;
import java.lang.reflect.Field;
import java.util.*;

/**
 * created  on 2018/11/18.
 */
abstract class SourceCreateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceCreateUtils.class);

    private static final EnumSet<ElementKind> FIELD_KINDS = EnumSet.of(ElementKind.FIELD);

    private static final String META_CLASS_NAME_SUFFIX = "_";

    private static final String JAVA_LANG = "java.lang";

    static final String PROP_PRE = "    public static final";


    /**
     * three key point :
     * <ol>
     *     <li>check required field , eg : {@link TableMeta#DOMAIN_PROPS}</li>
     *     <li>check property override</li>
     *     <li>check column duplication in a table</li>
     * </ol>
     *
     * @return property element set of {@code tableElement} .
     */
    @NonNull
    static Set<VariableElement> generateAttributes(@NonNull List<TypeElement> entityMappedElementList,
                                                   @NonNull List<TypeElement> parentMappedElementList) {

        try {
            Set<String> entityPropName = new HashSet<>();

            generateEntityAttributes(parentMappedElementList, entityPropName);

            final Set<VariableElement> entityPropSet = generateEntityAttributes(entityMappedElementList, entityPropName);
            // assert required props
            MetaAssert.assertRequiredMappingProps(entityMappedElementList, entityPropName);
            return entityPropSet;
        } catch (MetaException e) {
            LOG.error("entityMappedElementList:{}\nparentMappedElementList:{}",
                    entityMappedElementList, parentMappedElementList);
            throw e;
        }
    }

    /**
     * get entity element that annotated by {@link Table}
     */
    @Nullable
    static TypeElement entityElement(List<TypeElement> entityMappingElementList) {
        return CollectionUtils.isEmpty(entityMappingElementList)
                ? null
                : entityMappingElementList.get(entityMappingElementList.size() - 1);
    }

    /**
     * create meta source code import part
     *
     * @param entityElement       entity element that annotated by {@link Table}
     * @param parentEntityElement entityElement's parent element that annotated by {@link Table}
     * @param mappingPropElements entityElement's mapping prop collection
     * @return source code import part
     */
    static String generateImport(@NonNull TypeElement entityElement, @Nullable TypeElement parentEntityElement,
                                 @NonNull Collection<VariableElement> mappingPropElements) {
        StringBuilder builder = new StringBuilder("package ");
        // 1. source package part
        builder.append(getPackage(entityElement))
                .append(";\n\n")
        ;

        // 2. source army import part
        appendArmyClassImport(builder);

        // 3. source parent class import part
        appendParentClassImport(builder, entityElement, parentEntityElement);

        // 4. source mapping props class import part
        appendMappingPropsClassImport(builder, mappingPropElements);

        builder.append("\n\n");
        return builder.toString();
    }


    static String getPackage(TypeElement type) {
        return ((PackageElement) type.getEnclosingElement()).getQualifiedName().toString();
    }

    /**
     * create meta source code class definition part
     */
    static String generateClassDefinition(TypeElement entityElement, TypeElement parentEntityElement) {
        StringBuilder builder = new StringBuilder();

        builder.append("@Generated(value = \"")
                .append(ArmyMetaModelEntityProcessor.class.getName())
                .append("\")")
                .append("\n")

                .append("@StaticMetamodel(")
                .append(entityElement.getSimpleName())
                .append(".class)")
                .append("\n")

                .append("public abstract class ")
                .append(entityElement.getSimpleName())
                .append(META_CLASS_NAME_SUFFIX)
        ;

        if (parentEntityElement != null) {
            builder.append(" extends ")
                    .append(parentEntityElement.getSimpleName())
                    .append(META_CLASS_NAME_SUFFIX)
            ;

        }
        builder.append(" {\n\n");
        return builder.toString();
    }


    /**
     * generate meta source  class body
     */
    static String generateBody(@NonNull TypeElement entityElement, @Nullable TypeElement parentEntityElement,
                               @NonNull List<MetaAttribute> mappingPropList) {
        StringBuilder builder = new StringBuilder();

        //1.  TableMeta part
        appendTableMeta(entityElement, builder, parentEntityElement);

        Table table = entityElement.getAnnotation(Table.class);
        //2.  meta count part
        appendMetaCount(table, builder, parentEntityElement, mappingPropList);

        //3. prop names  definition
        appendMappingPropNames(builder, mappingPropList);

        // new line
        builder.append("\n\n");

        //4. mapping prop meta definition
        appendMappingPropMeta(builder, mappingPropList);

        builder.append("\n\n}\n\n\n");
        return builder.toString();
    }


    /*####################################### private method #################################################*/

    private static void appendTableMeta(@NonNull TypeElement entityElement, StringBuilder builder,
                                        @Nullable TypeElement parentEntityElement) {
        String format = "%s %s<%s> %s = new %s<>(%s%s.class);\n\n";
        String parentTableMetaText;
        if (parentEntityElement == null) {
            parentTableMetaText = "";
        } else {
            parentTableMetaText = String.format("%s%s.%s,",
                    parentEntityElement.getSimpleName(),
                    META_CLASS_NAME_SUFFIX,
                    MetaConstant.TABLE_PROP_NAME
            );
        }
        builder.append(String.format(format,
                PROP_PRE,
                TableMeta.class.getSimpleName(),
                entityElement.getSimpleName(),
                MetaConstant.TABLE_PROP_NAME,
                DefaultTable.class.getSimpleName(),
                parentTableMetaText,
                entityElement.getSimpleName()
        ));
    }

    private static void appendMetaCount(Table table, StringBuilder builder, @Nullable TypeElement parentEntityElement,
                                        @NonNull List<MetaAttribute> attributeList) {
        builder.append(PROP_PRE)
                .append(" String TABLE_NAME = \"")
                .append(table.name())
                .append("\";\n\n")

                .append(PROP_PRE)
                .append(" int FIELD_COUNT = ")
                .append(attributeList.size())
                .append(";\n\n")

                .append(PROP_PRE)
                .append(" int FIELD_TOTAL = ")
        ;

        if (parentEntityElement == null) {
            builder.append("FIELD_COUNT");
        } else {
            builder.append(parentEntityElement.getSimpleName())
                    .append(META_CLASS_NAME_SUFFIX)
                    .append(".FIELD_TOTAL")
            ;
        }
        builder.append(";\n\n");
    }

    private static void appendMappingPropNames(StringBuilder builder,
                                               @NonNull List<MetaAttribute> mappingPropMetaList) {
        int count = 0;
        for (MetaAttribute mappingPropMeta : mappingPropMetaList) {
            builder.append(mappingPropMeta.getNameDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }
    }

    private static void appendMappingPropMeta(StringBuilder builder, @NonNull List<MetaAttribute> attributeList) {
        int count = 0;
        for (MetaAttribute metaAttribute : attributeList) {
            builder.append(metaAttribute.getDefinition())
                    .append("\n");
            count++;
            if (count % 4 == 0) {
                builder.append("\n");
            }
        }
    }


    /**
     * <ul>
     *     <li>{@link Field#getName()} cannot duplication in Entity mapped by table</li>
     *     <li>{@link Column#name()} cannot duplication in table</li>
     * </ul>
     *
     * @param mappedClassElementList unmodifiable list
     * @param entityPropNameSet      modifiable set
     * @return unmodifiable set
     */
    private static Set<VariableElement> generateEntityAttributes(List<TypeElement> mappedClassElementList,
                                                                 Set<String> entityPropNameSet)
            throws MetaException {
        Set<VariableElement> mappedPropSet = new HashSet<>();
        Set<String> columnNameSet = new HashSet<>();
        List<String> discriminatorColumnList = new ArrayList<>(1);

        TypeElement entityElement = entityElement(mappedClassElementList);

        Column column;
        VariableElement mappedProp;
        String columnName;
        for (TypeElement mappedElement : mappedClassElementList) {

            for (Element element : mappedElement.getEnclosedElements()) {
                if (!FIELD_KINDS.contains(element.getKind())) {
                    continue;
                }
                mappedProp = (VariableElement) element;
                column = mappedProp.getAnnotation(Column.class);
                if (column == null) {
                    continue;
                }
                if (entityPropNameSet.contains(mappedProp.getSimpleName().toString())) {
                    throw MetaAssert.cratePropertyDuplication(mappedElement, mappedProp);
                }
                entityPropNameSet.add(mappedProp.getSimpleName().toString());

                columnName = columnName(mappedProp, column);
                if (columnNameSet.contains(columnName)) {
                    throw createColumnDuplication(mappedElement, columnName);
                }
                // assert io.army.annotation.Column
                MetaAssert.assertColumn(mappedElement, mappedProp, column, columnName);
                // assert io.army.annotation.DiscriminatorValue , io.army.annotation.Inheritance
                addDiscriminator(entityElement, mappedProp, columnName, discriminatorColumnList);

                columnNameSet.add(columnName);

                mappedPropSet.add(mappedProp);
            }
        }

        assertInheritance(entityElement, discriminatorColumnList);
        return Collections.unmodifiableSet(mappedPropSet);
    }

    private static void assertInheritance(TypeElement entityElement, List<String> discriminatorColumnList) {
        if (entityElement != null && entityElement.getAnnotation(Inheritance.class) != null) {
            Assert.notEmpty(discriminatorColumnList,
                    () -> String.format("entity[%s] discriminator column not exists.",
                            entityElement.getQualifiedName()));
        }
    }


    private static void addDiscriminator(TypeElement entityElement, VariableElement mappedProp, String columnName,
                                         List<String> discriminatorColumnList) throws MetaException {
        Assert.notNull(entityElement, "");

        Inheritance inheritance = entityElement.getAnnotation(Inheritance.class);
        if (inheritance == null) {
            return;
        }
        if (columnName.equals(inheritance.value())) {
            discriminatorColumnList.add(columnName);
        } else {
            return;
        }

        if (mappedProp.asType().getKind() != TypeKind.DECLARED) {
            MetaAssert.throwDiscriminatorNotEnum(entityElement, mappedProp);
        }
        Element element = ((DeclaredType) mappedProp.asType()).asElement();

        if (element.getKind() != ElementKind.ENUM) {
            MetaAssert.throwDiscriminatorNotEnum(entityElement, mappedProp);
        }
        if (!(element instanceof TypeElement)) {
            MetaAssert.throwDiscriminatorNotEnum(entityElement, mappedProp);
        }

        TypeElement enumElement = (TypeElement) element;
        boolean isCodeEnum = false;
        for (TypeMirror enumInterface : enumElement.getInterfaces()) {
            if (enumInterface.toString().equals(CodeEnum.class.getName())) {
                isCodeEnum = true;
                break;
            }
        }
        if (!isCodeEnum) {
            MetaAssert.throwDiscriminatorNotEnum(entityElement, mappedProp);
        }

    }


    private static String columnName(VariableElement mappedProp, Column column) {
        return StringUtils.hasText(column.name())
                ? column.name()
                : StringUtils.camelToLowerCase(mappedProp.getSimpleName().toString());

    }


    private static MetaException createColumnDuplication(TypeElement mappedElement, String columnName) {
        return new MetaException(ErrorCode.META_ERROR, String.format(
                "Mapped class[%s] mapping column[%s] duplication"
                , mappedElement.getQualifiedName(), columnName));
    }


    static String getQualifiedName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString() + META_CLASS_NAME_SUFFIX;
    }

    private static void appendParentClassImport(StringBuilder builder, TypeElement entityElement,
                                                TypeElement parentEntityElement) {
        if (parentEntityElement != null && !samePackage(entityElement, parentEntityElement)) {
            builder.append("import ")
                    .append(parentEntityElement.getQualifiedName())
                    .append(META_CLASS_NAME_SUFFIX)
                    .append(";\n")
            ;
        }

    }

    private static void appendArmyClassImport(StringBuilder builder) {
        builder
                .append("import ")
                .append(FieldMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(IndexFieldMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(Generated.class.getName())
                .append(";\n")

                .append("import ")
                .append(StaticMetamodel.class.getName())
                .append(";\n")

                .append("import ")
                .append(DefaultTable.class.getName())
                .append(";\n")

                .append("import ")
                .append(TableMeta.class.getName())
                .append(";\n\n")
        ;
    }

    private static void appendMappingPropsClassImport(StringBuilder builder,
                                                      Collection<VariableElement> mappingPropElements) {
        Set<String> fieldTypeNameSet = new HashSet<>();
        String fieldTypeName;
        for (VariableElement mappingProp : mappingPropElements) {
            fieldTypeName = mappingProp.asType().toString();
            if (fieldTypeNameSet.contains(fieldTypeName)
                    || isJavaLang(mappingProp.asType())) {
                continue;
            }
            builder.append("import ")
                    .append(fieldTypeName)
                    .append(";\n")
            ;
            fieldTypeNameSet.add(fieldTypeName);
        }
    }

    private static boolean samePackage(TypeElement type, TypeElement superType) {
        return type.getEnclosingElement().equals(superType.getEnclosingElement());
    }

    private static boolean isJavaLang(TypeMirror typeMirror) {
        String name = typeMirror.toString();
        int index = name.lastIndexOf('.');
        boolean match = false;
        if (index > 0) {
            match = JAVA_LANG.equals(name.substring(0, index));
        }
        return match;
    }
}
