package io.army.modelgen;

import io.army.annotation.Column;
import io.army.annotation.Inheritance;
import io.army.annotation.Table;
import io.army.criteria.impl.TableMetaFactory;
import io.army.lang.NonNull;
import io.army.lang.Nullable;
import io.army.meta.*;
import io.army.util.Assert;
import io.army.util.CollectionUtils;
import io.army.util.StringUtils;
import io.army.util.Times;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @since 1.0
 */
abstract class SourceCreateUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SourceCreateUtils.class);

    private static final EnumSet<ElementKind> FIELD_KINDS = EnumSet.of(ElementKind.FIELD);

    private static final String JAVA_LANG = "java.lang";

    private static final String MEMBER_PRE = "    ";

    static final String PROP_PRE = MEMBER_PRE + "public static final";

    static final String COMMENT_PRE = MEMBER_PRE;

    static final String ANNOTATION_PRE = "        ";


    /**
     * extract entity mapping property elements.
     * <p>
     * five key point :
     * <ol>
     *     <li>check required field , eg : {@link TableMeta#DOMAIN_PROPS}</li>
     *     <li>check property override</li>
     *     <li>check column duplication in a tableMeta</li>
     *     <li>check index filed validation</li>
     *     <li>check discriminator validation</li>
     * </ol>
     * </p>
     *
     * @return property element set of {@code tableElement} .
     */
    @NonNull
    static Set<VariableElement> generateAttributes(@NonNull List<TypeElement> entityMappedElementList,
                                                   @NonNull List<TypeElement> parentMappedElementList,
                                                   @NonNull Set<String> indexColumnNameSet) {

        try {
            Set<String> entityPropNameSet = new HashSet<>();
            Map<String,VariableElement> parentPropMap ,entityPropMap;
            //1.check parentMeta mapping then  add super entity props to entityPropNameSet to avoid child class duplicate prop
            parentPropMap = generateEntityAttributes(parentMappedElementList, entityPropNameSet, Collections.emptySet());
            // 2. extract entity property elements then check entity mapping
           entityPropMap = generateEntityAttributes(entityMappedElementList,
                    entityPropNameSet, indexColumnNameSet);

           Set<VariableElement> entityPropSet = new HashSet<>(entityPropMap.values());
           if(!parentPropMap.isEmpty()){
               VariableElement idProp = parentPropMap.get(TableMeta.ID);
               Assert.notNull(idProp,()-> String.format("entity[%s] no id",entityElement(parentMappedElementList)));
               entityPropSet.add(idProp);
           }
            // assert required props
            MetaUtils.assertRequiredMappingProps(entityMappedElementList, entityPropNameSet);

            MetaUtils.assertMappingModeParent(entityMappedElementList, parentMappedElementList);

            return Collections.unmodifiableSet(entityPropSet);
        } catch (MetaException e) {
            LOG.error("entityMappedElementList:{}\nparentMappedElementList:{}",
                    entityMappedElementList, parentMappedElementList, e);
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
     * debugSQL meta source code import part
     *
     * @param entityElement       entity element that annotated by {@link Table}
     * @param parentEntityElement entityElement's parentMeta element that annotated by {@link Table}
     * @param mappingPropElements entityElement's mapping prop collection
     * @return source code import part
     */
    static String generateImport(@NonNull TypeElement entityElement, @Nullable TypeElement parentEntityElement,
                                 @NonNull Collection<VariableElement> mappingPropElements) {
        StringBuilder builder = new StringBuilder("package ");
        // 1. source package part
        builder.append(getPackageName(entityElement))
                .append(";\n\n")
        ;

        // 2. source army import part
        appendArmyClassImport(builder);

        // 3. source parentMeta class import part
        appendParentClassImport(builder, entityElement, parentEntityElement);

        // 4. source mapping props class import part
        appendMappingPropsClassImport(builder, mappingPropElements);

        builder.append("\n\n");
        return builder.toString();
    }


    static String getPackageName(TypeElement entityElement) {
        /*
         * not doc
         * entityElement must be a top-level
         * @see ArmyMetaModelEntityProcessor#assertEntity(TypeElement, Set)
         */
        return ((PackageElement) entityElement.getEnclosingElement()).getQualifiedName().toString();
    }

    /**
     * debugSQL meta source code class definition part
     */
    static String generateClassDefinition(TypeElement entityElement, TypeElement parentEntityElement) {
        StringBuilder builder = new StringBuilder();

        builder.append("@Generated(value = \"")
                .append(ArmyMetaModelEntityProcessor.class.getName())
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",date = \"")
                .append(ZonedDateTime.now().format(Times.dateTimeFormatter(Times.FULL_ZONE_DATE_TIME_FORMAT)))
                .append("\"\n")
                .append(ANNOTATION_PRE)
                .append(",comments = \"")
                .append(entityElement.getAnnotation(Table.class).comment())
                .append("\")")
                .append("\n")
                .append("public abstract class ")
                .append(entityElement.getSimpleName())
                .append(MetaConstant.META_CLASS_NAME_SUFFIX)
        ;

        if (parentEntityElement != null) {
            builder.append(" extends ")
                    .append(parentEntityClassRef(entityElement, parentEntityElement))
                    .append(MetaConstant.META_CLASS_NAME_SUFFIX);
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

        //2.  meta count part
        appendMetaCount(entityElement, builder, parentEntityElement, mappingPropList);

        // 3. field count validate static method
        appendFieldCountValidateMethod(entityElement, builder);

        //4. prop names  definition
        appendMappingPropNames(builder, mappingPropList);

        // new line
        builder.append("\n\n");

        //5. mapping prop meta definition
        appendMappingPropMeta(builder, mappingPropList);

        builder.append("\n\n}\n\n\n");
        return builder.toString();
    }

    private static void appendFieldCountValidateMethod(TypeElement entityElement, StringBuilder builder) {
        builder.append("\n")
                .append(MEMBER_PRE)
                .append("static {\n")
                .append(MEMBER_PRE)
                .append("\t")

                .append(Assert.class.getSimpleName())
                .append(".state(")
                .append(MetaConstant.TABLE_META)
                .append(".fieldCollection().size() == ")
                .append(MetaConstant.FIELD_COUNT)

                .append(",()->\n")
                .append(MEMBER_PRE)
                .append("\t\t")
                .append("String.format(\"domain[%s] field count[%s] error.\",")
                .append(entityElement.getSimpleName())
                .append(".class.getName(),")
                .append(MetaConstant.FIELD_COUNT)
                .append("));\n\t}\n\n")
        ;
    }


    static String columnName(TypeElement entityElement, VariableElement mappedProp, Column column) {
        MetaUtils.assertReservedColumnName(entityElement, mappedProp, column);
        return StringUtils.hasText(column.name())
                ? column.name()
                : StringUtils.camelToLowerCase(mappedProp.getSimpleName().toString());

    }

    static String getQualifiedName(TypeElement typeElement) {
        return typeElement.getQualifiedName().toString() + MetaConstant.META_CLASS_NAME_SUFFIX;
    }


    /*################################## blow private method ##################################*/

    private static void appendTableMeta(@NonNull TypeElement entityElement, StringBuilder builder,
                                        @Nullable TypeElement parentEntityElement) {
        String format = "%s %s<%s> %s = TableMetaFactory.%s(%s%s.class);\n\n";
        String parentTableMetaText, methodName, tableMetaName;
        if (parentEntityElement == null) {
            if (entityElement.getAnnotation(Inheritance.class) == null) {
                methodName = "createTableMeta";
                tableMetaName = TableMeta.class.getSimpleName();
            } else {
                methodName = "createParentTableMta";
                tableMetaName = ParentTableMeta.class.getSimpleName();
            }
            parentTableMetaText = "";
        } else {
            methodName = "createChildTableMeta";
            tableMetaName = ChildTableMeta.class.getSimpleName();
            parentTableMetaText = String.format("%s%s.%s,",
                    parentEntityClassRef(entityElement, parentEntityElement),
                    MetaConstant.META_CLASS_NAME_SUFFIX,
                    MetaConstant.TABLE_META
            );
        }
        builder.append(String.format(format,
                PROP_PRE,
                tableMetaName,
                entityElement.getSimpleName(),
                MetaConstant.TABLE_META,
                methodName,
                parentTableMetaText,
                entityElement.getSimpleName()
        ));
    }

    private static void appendMetaCount(TypeElement entityElement, StringBuilder builder
            , @Nullable TypeElement parentEntityElement, List<MetaAttribute> attributeList) {
        Table table = entityElement.getAnnotation(Table.class);

        builder.append(PROP_PRE)
                .append(" String ")
                .append(MetaConstant.TABLE_NAME)
                .append(" = \"")
                .append(table.name())
                .append("\";\n\n");

        builder.append(PROP_PRE)
                .append(" int ")
                .append(MetaConstant.FIELD_COUNT)
                .append(" = ");

        builder.append(attributeList.size());

        builder.append(";\n\n")
                .append(PROP_PRE)
                .append(" int ")
                .append(MetaConstant.FIELD_TOTAL)
                .append(" = ")
                .append(MetaConstant.FIELD_COUNT)
        ;

        if (parentEntityElement != null) {
            builder.append(" + ")
                    .append(parentEntityClassRef(entityElement, parentEntityElement))
                    .append(MetaConstant.META_CLASS_NAME_SUFFIX)
                    .append(".")
                    .append(MetaConstant.FIELD_TOTAL)
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
     *     <li>{@link Field#getName()} cannot duplication in Entity mapped by tableMeta</li>
     *     <li>{@link Column#name()} cannot duplication in a tableMeta</li>
     * </ul>
     *
     * @param mappedClassElementList unmodifiable list
     * @param entityPropNameSet      modifiable set
     * @param indexColumnNameSet     a unmodifiable set
     * @return a unmodifiable map
     */
    private static Map<String,VariableElement> generateEntityAttributes(List<TypeElement> mappedClassElementList,
                                                                 Set<String> entityPropNameSet,
                                                                 Set<String> indexColumnNameSet)
            throws MetaException {
        Map<String, VariableElement> mappedPropMap = new HashMap<>();
        // column name set to avoid duplication
        Set<String> columnNameSet = new HashSet<>();
        List<String> discriminatorColumnList = new ArrayList<>(1);

        final TypeElement domainElement = entityElement(mappedClassElementList);
        final String discriminatorColumn = discriminatorColumnName(domainElement);
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
                // assert prop name not duplication
                MetaUtils.assertMappingPropNotDuplication(domainElement, mappedProp.getSimpleName().toString()
                        , entityPropNameSet);

                columnName = columnName(domainElement, mappedProp, column);
                // make column name lower case
                columnName = StringUtils.toLowerCase(columnName);
                if (columnNameSet.contains(columnName)) {
                    throw MetaUtils.createColumnDuplication(mappedElement, columnName);
                }
                // assert io.army.annotation.Column
                MetaUtils.assertColumn(domainElement, mappedProp, column, columnName, discriminatorColumn);
                // assert io.army.annotation.DiscriminatorValue , io.army.annotation.Inheritance
                addDiscriminator(domainElement, mappedProp, columnName, discriminatorColumnList);

                columnNameSet.add(columnName);

                mappedPropMap.put(mappedProp.getSimpleName().toString(), mappedProp);
            }
        }
        MetaUtils.assertIndexColumnNameSet(domainElement, columnNameSet, indexColumnNameSet);
        MetaUtils.assertInheritance(domainElement, discriminatorColumnList);
        return Collections.unmodifiableMap(mappedPropMap);
    }


    private static void addDiscriminator(TypeElement entityElement, VariableElement mappedProp, String columnName,
                                         List<String> discriminatorColumnList) throws MetaException {
        Assert.notNull(entityElement, "entityElement required");

        Inheritance inheritance = entityElement.getAnnotation(Inheritance.class);
        if (inheritance == null) {
            return;
        }
        if (columnName.equals(inheritance.value())) {
            discriminatorColumnList.add(columnName);
        } else {
            return;
        }

        if (!MetaUtils.isCodeEnum(mappedProp)) {
            MetaUtils.throwDiscriminatorNotEnum(entityElement, mappedProp);
        }

    }

    private static void appendParentClassImport(StringBuilder builder, TypeElement entityElement,
                                                TypeElement parentEntityElement) {
        if (parentEntityElement == null) {
            return;
        }
        if (!sameClassName(entityElement, parentEntityElement)
                && !samePackage(entityElement, parentEntityElement)) {
            builder.append("import ");

            builder.append(parentEntityElement.getQualifiedName());

            builder.append(MetaConstant.META_CLASS_NAME_SUFFIX)
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

                .append("import javax.annotation.Generated")
                .append(";\n")

                .append("import ")
                .append(TableMetaFactory.class.getName())
                .append(";\n")

                .append("import ")
                .append(TableMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(ParentTableMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(ChildTableMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(UniqueFieldMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(PrimaryFieldMeta.class.getName())
                .append(";\n")

                .append("import ")
                .append(Assert.class.getName())
                .append(";\n\n")
        ;
    }

    private static void appendMappingPropsClassImport(StringBuilder builder,
                                                      Collection<VariableElement> mappingPropElements) {
        // set to avoid field type duplicate
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


    private static boolean samePackage(TypeElement entityElement, TypeElement parentEntityElement) {
        /*
         * not doc
         * entityElement must be a top-level
         * @see ArmyMetaModelEntityProcessor#assertEntity(TypeElement, Set).
         */
        return entityElement.getEnclosingElement().equals(parentEntityElement.getEnclosingElement());
    }

    private static boolean sameClassName(TypeElement entityElement, TypeElement parentEntityElement) {
        return entityElement.getSimpleName().equals(parentEntityElement.getSimpleName());
    }

    /**
     * @return reference of parentMeta entity class name
     */
    private static String parentEntityClassRef(TypeElement entityElement, TypeElement parentEntityElement) {
        String parentRef;
        if (sameClassName(entityElement, parentEntityElement)) {
            parentRef = parentEntityElement.getQualifiedName().toString();
        } else {
            parentRef = parentEntityElement.getSimpleName().toString();
        }
        return parentRef;
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

    /**
     * extract entity's discriminator column name
     *
     * @return lower case column name
     */
    @Nullable
    private static String discriminatorColumnName(TypeElement entityElement) {
        if (entityElement == null) {
            return null;
        }
        Inheritance inheritance = entityElement.getAnnotation(Inheritance.class);
        String columnName;
        if (inheritance == null) {
            columnName = null;
        } else {
            columnName = StringUtils.toLowerCase(inheritance.value());
        }
        return columnName;
    }
}
